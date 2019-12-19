package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class DatabaseHelper extends SQLiteOpenHelper {

    public  static final String dbName = "170163P";
    public  static final String account = "account_table";
    public  static final String transaction = "transaction_table";



    public DatabaseHelper(Context context) {
        super(context, dbName, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String q1 = "create table "+account+" (accountNo TEXT(50) PRIMARY KEY,bankName TEXT(50),accountHolderName TEXT(50),balance REAL) ";
        String q2 =" create table "+transaction+" (accountNo TEXT(50) ,date date, expenseType TEXT(20),amount REAL,FOREIGN KEY (accountNo) REFERENCES "+account+"(accountNo))";
        sqLiteDatabase.execSQL(q1);
        sqLiteDatabase.execSQL(q2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String q1 = "DROP TABLE IF EXISTS "+account;
        String q2 ="DROP TABLE IF EXISTS "+transaction;
        sqLiteDatabase.execSQL(q1);
        sqLiteDatabase.execSQL(q2);
        onCreate(sqLiteDatabase);
    }

    public boolean insertAccount(Account acc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",acc.getAccountNo());
        contentValues.put("bankName",acc.getBankName());
        contentValues.put("accountHolderName",acc.getAccountHolderName());
        contentValues.put("balance",acc.getBalance());
        long result = db.insert(account,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean updateAccount(Account acc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",acc.getAccountNo());
        contentValues.put("bankName",acc.getBankName());
        contentValues.put("accountHolderName",acc.getAccountHolderName());
        contentValues.put("balance",acc.getBalance());
        long result = db.update(account,contentValues,"accountNo = ?",new String[]{acc.getAccountNo()});
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }


    public Account getAccount(String accNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+account+" WHERE accountNo = ?",new String[]{accNumber});
        Account account = null;
        if(res.getCount() == 0){
            return account;
        }else{
            while(res.moveToNext()){
                String accountNo = res.getString(0);
                String bankName = res.getString(1);
                String accountHolderName = res.getString(2);
                double balance = res.getDouble(3);
                account = new Account(accountNo,bankName,accountHolderName,balance);
            }
            return account;
        }
    }

    public ArrayList<Account> getAllAccounts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+account,null);
        ArrayList<Account> accountList=new ArrayList<>();
        if(res.getCount()==0){
            return accountList;
        }else{

            while(res.moveToNext()){
                String accountNo = res.getString(0);
                String bankName = res.getString(1);
                String accountHolderName = res.getString(2);
                double balance = res.getDouble(3);
                accountList.add(new Account(accountNo,bankName,accountHolderName,balance));
            }
            return accountList;
        }
    }

    public boolean deleteAccount(String accountNo){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(account,"accountNo = "+accountNo,null) > 0;

    }

    public boolean logTransaction(Transaction t){

        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",t.getAccountNo());
        contentValues.put("date",format.format(t.getDate()));
        contentValues.put("expenseType",t.getExpenseType().toString());
        contentValues.put("amount",t.getAmount());


        long res = db.insert(transaction,null,contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }



    }

    public ArrayList<Transaction> getTransactions(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+transaction,null);
        return populateTransactions(res);
    }

    public ArrayList<Transaction> getTransactions(int limit){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+transaction+" LIMIT "+limit,null);
        return populateTransactions(res);
    }



    private ArrayList<Transaction> populateTransactions(Cursor res){

        ArrayList<Transaction> transactionList=new ArrayList<>();
        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);
        if(res.getCount()==0){
            return transactionList;
        }else{

            while(res.moveToNext()){
                String accountNo = res.getString(0);
                Date date = new Date();
                ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                try {
                    date =  format.parse(res.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
//

                double amount = res.getDouble(3);
                transactionList.add(new Transaction(date,accountNo,expenseType,amount));
            }
            return transactionList;
        }
    }
}
