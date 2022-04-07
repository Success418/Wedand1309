package co.za.wedwise.checklist;

import android.database.Cursor;
import android.webkit.URLUtil;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Common {
    public static String getNowDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static String getNowDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String convDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String convYearMonth(Date date) {
        return new SimpleDateFormat("yyyy-MM").format(date);
    }

    /*public static String convUSDate(String date) {
        Calendar.getInstance().set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
        return String.format("%s, %s %d, %d", new Object[]{convUSDay(cdate.get(7) - 1), convUSMonth(dMonth), Integer.valueOf(dDate), Integer.valueOf(dYear)});
    }*/

    public static String convUSMonth(int month) {
        return new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}[month];
    }

    public static String convUSDay(int day) {
        return new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}[day];
    }

    public static String genYear(String year) {
        if (year == null || year.length() == 0) {
            return "0000";
        }
        return year;
    }

    public static String getTodayStr() {
        Calendar ctoday = Calendar.getInstance();
        int tYear = ctoday.get(1);
        int tMonth = ctoday.get(2) + 1;
        int tDate = ctoday.get(5);
        return String.format("%s-%s-%s", new Object[]{twoNumber(tYear), twoNumber(tMonth), twoNumber(tDate)});
    }

    public static int getDday(String date) {
        Calendar ctoday = Calendar.getInstance();
        ctoday.set(ctoday.get(1), ctoday.get(2), ctoday.get(5));
        Calendar cddate = Calendar.getInstance();
        cddate.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
        return ((int) (ctoday.getTimeInMillis() / 86400000)) - ((int) (cddate.getTimeInMillis() / 86400000));
    }

    public static int getDday(int dyear, int dmonth, int ddate) {
        Calendar ctoday = Calendar.getInstance();
        ctoday.set(ctoday.get(1), ctoday.get(2), ctoday.get(5));
        Calendar cddate = Calendar.getInstance();
        cddate.set(dyear, dmonth - 1, ddate);
        return ((int) (ctoday.getTimeInMillis() / 86400000)) - ((int) (cddate.getTimeInMillis() / 86400000));
    }

    public String getDay(String date) {
        int tyear = Integer.parseInt(date.substring(0, 4));
        int tmonth = Integer.parseInt(date.substring(5, 7));
        int tdate = Integer.parseInt(date.substring(8, 10));
        Calendar cddate = Calendar.getInstance();
        cddate.set(tyear, tmonth - 1, tdate);
        return new String[]{"S", "M", "T", "W", "T", "F", "S"}[cddate.get(7)];
    }

    public static String checkZero(String value) {
        if (value.equals("0")) {
            return "";
        }
        return value;
    }

    public static boolean checkValue(String value) {
        if (value == null || value.length() == 0) {
            return false;
        }
        return true;
    }

    public static boolean checkValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = null;
        if (date == null || date.length() == 0) {
            return false;
        }
        try {
            format = sdf.format(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.equals(format);
    }

    public static boolean normalizeDateFormat(String date) {
        return true;
    }

    public static boolean checkValidUrl(String url) {
        if (URLUtil.isValidUrl(url)) {
            return true;
        }
        return false;
    }

    public static String numberFormat(int data) {
        String data_str = Integer.toString(data);
        String number = "";
        String comma = ",";
        int len = data_str.length();
        int k = 3 - (len % 3);
        for (int i = 0; i < len; i++) {
            number = new StringBuilder(String.valueOf(number)).append(data_str.charAt(i)).toString();
            if (i < len - 1) {
                k++;
                if (k % 3 == 0) {
                    number = new StringBuilder(String.valueOf(number)).append(comma).toString();
                    k = 0;
                }
            }
        }
        return number;
    }

    public static String numberFormat(String data) {
        String data_str = data;
        String number = "";
        String comma = ",";
        int len = data_str.length();
        int k = 3 - (len % 3);
        for (int i = 0; i < len; i++) {
            number = new StringBuilder(String.valueOf(number)).append(data_str.charAt(i)).toString();
            if (i < len - 1) {
                k++;
                if (k % 3 == 0) {
                    number = new StringBuilder(String.valueOf(number)).append(comma).toString();
                    k = 0;
                }
            }
        }
        return number;
    }

    public static String twoNumber(String data) {
        if (data == null || data.length() == 0) {
            return "0";
        }
        String number = "";
        if (Integer.parseInt(data) >= 10) {
            return data;
        }
        return String.format("0%d", new Object[]{Integer.valueOf(Integer.parseInt(data))});
    }

    public static String twoNumber(int data) {
        if (data < 10) {
            return String.format("0%d", new Object[]{Integer.valueOf(data)});
        }
        return String.format("%d", new Object[]{Integer.valueOf(data)});
    }

    public static HashMap<String, String> cursor2HashMap(Cursor cursor) {
        HashMap<String, String> hm = new HashMap();
        hm.put("count", "0");
        cursor.getColumnNames();
        int i = 0;
        while (cursor.moveToNext()) {
            try {
                for (int j = 0; j < cursor.getColumnCount(); j++) {
                    String fieldName = cursor.getColumnName(j);
                    hm.put(new StringBuilder(String.valueOf(fieldName)).append("[").append(i).append("]").toString(), cursor.getString(j));
                }
                i++;
            } catch (Exception e) {
            }
        }
        hm.put("count", Integer.toString(i));
        return hm;
    }

    public static HashMap<String, String> xml2HashMap(String tmpData, String encoding) {
        HashMap<String, String> hm = new HashMap();
        hm.put("count", "0");
        try {
            NodeList dataList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(tmpData.getBytes(encoding))).getDocumentElement().getElementsByTagName("data");
            int c = 0;
            for (int i = 0; i < dataList.getLength(); i++) {
                NodeList dataNodeList = dataList.item(i).getChildNodes();
                for (int j = 0; j < dataNodeList.getLength(); j++) {
                    Node itemNode = dataNodeList.item(j);
                    if (itemNode.getFirstChild() != null) {
                        String nodeName = itemNode.getNodeName();
                        hm.put(new StringBuilder(String.valueOf(nodeName)).append("[").append(i).append("]").toString(), itemNode.getFirstChild().getNodeValue());
                    }
                }
                c++;
            }
            hm.put("count", Integer.toString(c));
        } catch (Exception e) {
        }
        return hm;
    }
}
