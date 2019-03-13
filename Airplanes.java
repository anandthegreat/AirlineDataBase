import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Flights{
    private ArrayList<Integer> passengerListforFlight;
    private int freeSeats;
    private int bookedSeats;
    private int flightNumber;
    private int totalSeats;
    
    public Flights(int seats,int flightNumber){
        passengerListforFlight=new ArrayList<>(seats);          //all of them are initialized with zero
        freeSeats=seats;
        bookedSeats=0;
        totalSeats=seats;
        this.flightNumber=flightNumber;
    }

	public void reserveInFlights(int id){
        freeSeats--;
        bookedSeats++;
        passengerListforFlight.add(id);
    }

    public void cancelFromFlight(int id){
        int totalSeats=bookedSeats+freeSeats;
        if(passengerListforFlight.contains(id))
        {	passengerListforFlight.remove(new Integer(id));
            freeSeats++;
            bookedSeats--;
        }
        else System.out.println("No passenger with id: "+id+" present in this flight");
    }

    public ArrayList<Integer> getPassengerListforFlight() {
        return passengerListforFlight;
    }

    public int getBookedSeats(){            //for Total_Reservations()
        return this.bookedSeats;
    }

    public int getTotalSeats(){
        return this.totalSeats;
    }

    public int getFreeSeats(){
        return this.freeSeats;
    }
}

class Passenger{
	private int id;
	private ArrayList<Integer> myFlights;
    
    public Passenger(int id){
      this.id=id;
      this.myFlights = new ArrayList<Integer>();
    }

    public void setId(int id) {
		this.id = id;
	}

	public void setMyFlights(ArrayList<Integer> myFlights) {
		this.myFlights = myFlights;
	}

	public void cancelFromPassenger(int F){
    	if(myFlights.contains(F)==false) System.out.println("You didn't book on this flight.");
    	else this.myFlights.remove(new Integer(F));
    }

    public void reserveInPassenger(int F){
    	if(!myFlights.contains(F)) this.myFlights.add(F);
    }

    public int getId(){
        return this.id;
    }

    public ArrayList<Integer> getMyFlights(){
        return this.myFlights;
    }
}

class Database{
    private Flights[] flightList;										//database of flights and data related to it.
    private Passenger[] passengerListforDatabase;						//database of all passenger on all flights.

    public Database(int noOfFlights){
        flightList= new Flights[noOfFlights+1];
        passengerListforDatabase = new Passenger[10000];

        Random r =new Random();
        for(int i=0;i<noOfFlights+1;i++){
            flightList[i]=new Flights(5+r.nextInt(11),i);      //initialize flights with random no of seats
        }
    }
 
    public Flights[] getFlightList() {
		return flightList;
	}

	public void setFlightList(Flights[] flightList) {
		this.flightList = flightList;
	}

	public Passenger[] getPassengerList() {
		return passengerListforDatabase;
	}

	public void setPassengerList(Passenger[] passengerListforDatabase) {
		this.passengerListforDatabase = passengerListforDatabase;
	}
}

//===========================================================================================

public class Airplanes {
    public static void main(String[] args){
    	Database database=new Database(5);
    	ConcurrencyControlManager CCM= new ConcurrencyControlManager(database);
    	Thread[] threads=new Thread[10];
    	
    	ExecutorService pool=Executors.newFixedThreadPool(10);
    	
    	Transaction[] transaction=new Transaction[100];
    	
    	long timerStart=System.currentTimeMillis();
    	for(int i=0;i<20;i++) {
    		transaction[i]=new Transaction(database,CCM);
    		pool.execute(transaction[i]);
    	}
    	pool.shutdown();
    	long timerEnd=System.currentTimeMillis();
    	while(!pool.isTerminated())
    		timerEnd=System.currentTimeMillis();
    	System.out.println("Time elapsed: " +(timerEnd-timerStart));
    }
}










