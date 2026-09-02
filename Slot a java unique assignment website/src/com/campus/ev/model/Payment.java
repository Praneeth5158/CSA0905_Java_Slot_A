package com.campus.ev.model;

import java.sql.Timestamp;

public class Payment {
    private int paymentId;
    private String invoiceNumber;
    private int sessionId;
    private int userId;
    private double amount;
    private String paymentMethod; // CAMPUS_WALLET, UPI_QR, SMART_ID_CARD, STUDENT_PORTAL, WAIVED_FLEET
    private String transactionRef;
    private String paymentStatus; // PAID, PENDING, FAILED, REFUNDED
    private Timestamp paymentTime;

    // Joined helper fields
    private String userName;
    private String userCode;
    private String sessionCode;

    public Payment() {}

    public Payment(int paymentId, String invoiceNumber, int sessionId, int userId, 
                   double amount, String paymentMethod, String transactionRef, 
                   String paymentStatus, Timestamp paymentTime) {
        this.paymentId = paymentId;
        this.invoiceNumber = invoiceNumber;
        this.sessionId = sessionId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionRef = transactionRef;
        this.paymentStatus = paymentStatus;
        this.paymentTime = paymentTime;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Timestamp getPaymentTime() { return paymentTime; }
    public void setPaymentTime(Timestamp paymentTime) { this.paymentTime = paymentTime; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }

    @Override
    public String toString() {
        return invoiceNumber + " (₹" + String.format("%.2f", amount) + " - " + paymentStatus + ")";
    }
}
