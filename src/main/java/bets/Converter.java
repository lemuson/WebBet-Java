package bets;

import java.util.Base64;

public class Converter
{
    public static String convertToBase64(byte[] logoData)
    {
        if (logoData == null || logoData.length == 0)
            return null;

        return Base64.getEncoder().encodeToString(logoData);
    }
}
