package banking;
import java.sql.*;

import org.sqlite.SQLiteDataSource;
import java.util.Scanner;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        BankDatabaseTerminal terminal = new BankDatabaseTerminal(getFileNameFromCLI(args));
        terminal.start();
    }

    private static String getFileNameFromCLI(String[] args) {
        String fileName = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fileName")) fileName = args[i+1];
        }
        return fileName;
    }
}

class BankDatabaseTerminal {
    private String url;
    private SQLiteDataSource dataSource = new SQLiteDataSource();
    private int loggedId;
    private Scanner scan;
    private Random random;

    public BankDatabaseTerminal(String fileName) {
        this.url = "jdbc:sqlite:" + fileName;
        this.dataSource.setUrl(url);
        this.loggedId = -1;
        this.scan = new Scanner(System.in);
        this.random = new Random();
        //Database and table 'card' are created at the very start of BankDatabaseTerminal
        try (Connection con = dataSource.getConnection()) {
            try (Statement stat = con.createStatement()) {
                stat.execute("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while(true) {
            if (this.loggedId == -1) {
                //User is not logged in
                System.out.println(
                        "1. Create an account\n" +
                        "2. Log into account\n" +
                        "0. Exit");
                int input = scan.nextInt();
                if (input == 0) {
                    System.out.println("Bye!");
                    break;
                }
                else if (input == 1) createCard();
                else if (input == 2) logIn();
            }
            else {
                //User is logged in, ID of logged account is this.loggedId
                System.out.println(
                        "1. Balance\n" +
                        "2. Add income\n" +
                        "3. Do transfer\n" +
                        "4. Close account\n" +
                        "5. Log out\n" +
                        "0. Exit");
                int input = scan.nextInt();
                if (input == 0) {
                    System.out.println("Bye!");
                    break;
                }
                else if (input == 1) balance();
                else if (input == 2) addIncome();
                else if (input == 3) doTransfer();
                else if (input == 4) closeAccount();
                else if (input == 5) logOut();
            }
        }
    }

    private void createCard() {
        //Create card-number as String
        StringBuilder cnStr = new StringBuilder();
        cnStr.append(400000);
        for (int i=0; i<9; i++) {
            cnStr.append(random.nextInt(10));
        }
        cnStr.append(luhnChecksum(cnStr.toString()));
        String cardNumber = cnStr.toString();

        //Create PIN as String
        StringBuilder pStr = new StringBuilder();
        for (int i=0; i<4; i++) {
            pStr.append(random.nextInt(10));
        }
        String pin = pStr.toString();

        //Create ID and balance, it will be set later
        int id = 1;
        int balance = 0;

        //Insert created values into 'card' table
        String insert = "INSERT INTO card VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection()) {
            try (Statement stat = con.createStatement();
                 PreparedStatement insertCard = con.prepareStatement(insert)) {
                //Find the highest ID by now
                try (ResultSet maxId = stat.executeQuery("SELECT MAX(id) FROM card")) {
                    if (maxId.next()) {
                        id = maxId.getInt(1) + 1;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //Insert
                insertCard.setInt(1, id);
                insertCard.setString(2, cardNumber);
                insertCard.setString(3, pin);
                insertCard.setInt(4, balance);
                insertCard.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(
                "Your card has been created\n" +
                "Your card number: \n" +
                cardNumber +"\n" +
                "Your card PIN: \n" +
                pin);
    }

    //Takes in 15-digits number and returns to-be-appended 16th digit based on algorithm
    private int luhnChecksum(String cardNumber) {
        char[] digits = cardNumber.toCharArray();
        int sum = 0;
        for (int i = 0; i < 15; i++) {
            int d = Character.getNumericValue(digits[i]);
            if (i % 2 == 0) d *= 2;
            if (d >= 10) d -= 9;
            sum += d;
        }
        return (10 - sum % 10) % 10;
    }

    private void logIn() {
        System.out.println("Enter your card number:");
        String testNumber = scan.next();
        System.out.println("Enter your PIN:");
        String testPin = scan.next();

        //Check if there is number matching PIN
        try (Connection con = dataSource.getConnection()) {
            try (Statement stat = con.createStatement()) {
                try (ResultSet card = stat.executeQuery("SELECT * FROM card")) {
                    while(card.next()) {
                        String number = card.getString("number");
                        String pin = card.getString("pin");
                        if (testNumber.equals(number) && testPin.equals(pin)) {
                            System.out.println("You have successfully logged in!");
                            this.loggedId = card.getInt("id");
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Inform if no match was found
        if (this.loggedId == -1) {
            System.out.println("Wrong card number or PIN!");
        }
    }

    private void logOut() {
        this.loggedId = -1;
        System.out.println("You have successfully logged out!");
    }

    private void balance() {
        try (Connection con = dataSource.getConnection()) {
            try (Statement stat = con.createStatement()) {
                try (ResultSet card = stat.executeQuery("SELECT id, balance FROM card")) {
                    while (card.next()) {
                        if (card.getInt("id") == this.loggedId) {
                            System.out.println("Balance: " + card.getInt("balance"));
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addIncome() {
        System.out.println("Enter income:");
        try (Connection con = dataSource.getConnection()) {
            transferTo(con, loggedId, scan.nextInt());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Income was added!");
    }

    //Update card table with 'funds' value for given receiver
    private void transferTo (Connection con, int id, int funds) {
        String update = "UPDATE card SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement updateCard = con.prepareStatement(update)) {
            updateCard.setInt(1, funds);
            updateCard.setInt(2, id);
            updateCard.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void doTransfer() {
        System.out.println("Transfer\nEnter card number:");
        String cardNumber = scan.next();
        int receiverId = 0;
        try (Connection con = dataSource.getConnection()) {

            //Check if card passes Luhn test and if it exists
            int lastDigit = Character.getNumericValue(cardNumber.charAt(cardNumber.length() - 1));
            if (luhnChecksum(cardNumber) != lastDigit) {
                System.out.println("Probably you made a mistake in the card number. Please try again!");
                return;
            }
            try (Statement stat = con.createStatement()) {
                try (ResultSet idFound = stat.executeQuery("SELECT id FROM card WHERE number='" + cardNumber + "'")) {
                    if (!idFound.next()) {
                        System.out.println("Such a card does not exist.");
                        return;
                    }
                    receiverId = idFound.getInt(1);
                    if (receiverId == this.loggedId) {
                        System.out.println("You can't transfer money to the same account!");
                        return;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //Check if enough funds
            System.out.println("Enter how much money you want to transfer:");
            int funds = scan.nextInt();
            try (Statement stat = con.createStatement()) {
                try (ResultSet loggedBalance = stat.executeQuery("SELECT balance FROM card WHERE id='" + this.loggedId + "'")) {
                    if (funds > loggedBalance.getInt(1)) {
                        System.out.println("Not enough money!");
                        return;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //Do transfer
            con.setAutoCommit(false);
            transferTo(con, receiverId, funds);
            transferTo(con, this.loggedId, -funds);
            con.commit();
            System.out.println("Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeAccount() {
        String delete = "DELETE FROM card WHERE id = ?";
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement deleteStatement = con.prepareStatement(delete)) {
                deleteStatement.setInt(1, this.loggedId);
                deleteStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.loggedId = -1;
        System.out.println("The account has been closed!");
    }
}
