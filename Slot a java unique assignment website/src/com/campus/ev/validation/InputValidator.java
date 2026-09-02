package com.campus.ev.validation;

import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s-]{8,15}$");
    private static final Pattern VEHICLE_NUM_PATTERN = Pattern.compile("^[A-Z0-9-]{4,16}$");

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    public static void validateEmail(String email) {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format (e.g. user@university.edu).");
        }
    }

    public static void validatePhone(String phone) {
        validateNotEmpty(phone, "Phone number");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format (e.g. +91 9876543210).");
        }
    }

    public static void validateVehicleNumber(String vehicleNumber) {
        validateNotEmpty(vehicleNumber, "Vehicle registration number");
        String clean = vehicleNumber.trim().toUpperCase();
        if (!VEHICLE_NUM_PATTERN.matcher(clean).matches()) {
            throw new IllegalArgumentException("Invalid vehicle number format (e.g. KA-01-EV-2024).");
        }
    }

    public static double parsePositiveDouble(String text, String fieldName) {
        validateNotEmpty(text, fieldName);
        try {
            double val = Double.parseDouble(text.trim());
            if (val <= 0) {
                throw new IllegalArgumentException(fieldName + " must be a positive number greater than 0.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid numeric value.");
        }
    }

    public static int parsePositiveInt(String text, String fieldName) {
        validateNotEmpty(text, fieldName);
        try {
            int val = Integer.parseInt(text.trim());
            if (val <= 0) {
                throw new IllegalArgumentException(fieldName + " must be an integer greater than 0.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid integer.");
        }
    }
}
