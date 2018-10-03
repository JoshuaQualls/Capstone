import java.sql.Timestamp;

public class AndroidProficiency01{
  public static void main(String[] args){
    Long tsLong = System.currentTimeMillis();
    Timestamp t = new Timestamp(tsLong);
    
    String tsString = t.toString();
    String sHour = tsString.substring(11,13);
    int hour = Integer.parseInt(sHour);
    
    
    if(hour >= 6 && hour < 12){
      System.out.println("Good Morning!");
    }
    else if(hour >= 12 && hour < 19){
      System.out.println("Good Afternoon!");
    }
    else{
      System.out.println("Good Evening!");
    }
    
  }
}