package hu.agoston.contractmanager.util;

public class TaxNumberValidator {

    private TaxNumberValidator() {
    }

    public static boolean isValid(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }

        // Normalizálás: csak számjegyek
        String taxNumber = normalize(input);

        // Pontosan 11 számjegy kell
        if (taxNumber.length() != 11) {
            return false;
        }

        // Részek
        String base = taxNumber.substring(0, 8);
        char vatCode = taxNumber.charAt(8);
        String countyCode = taxNumber.substring(9, 11);

        // alap ellenőrzések
        if (!base.matches("\\d{8}")) {
            return false;
        }

        // ÁFA-kód: 1–9 TELJESEN OK
        if (vatCode < '1' || vatCode > '9') {
            return false;
        }

        // Megye kód: 01–99 (NAV így kezeli)
        try {
            int county = Integer.parseInt(countyCode);
            return county >= 1 && county <= 99;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String normalize(String input) {
        return input.replaceAll("[^0-9]", "");
    }
}
