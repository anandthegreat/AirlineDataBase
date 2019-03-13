import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Flights{
    ArrayList<Integer> passengerListforFlight;
    int freeSeats;
    int bookedSeats;
    int flightNumber;
    int totalSeats;
    yoLock lock;

    public Flights(int seats,int flightNumber){
        passengerListforFlight=new ArrayList<>(seats);          //all of them are initialized with zero
        freeSeats=seats;
        bookedSeats=0;
        totalSeats=seats;
        this.flightNumber=flightNumber;
        lock=new yoLock();
    }

    public void reserveInFlights(int id){
        freeSeats--;
        bookedSeats++;
        passengerListforFlight.add(id);
        // System.out.println("Confirmation from flight "+ flightNumber +" - reservation successful");
    }

    public void cancelFromFlight(int id){
        int totalSeats=bookedSeats+freeSeats;
        if(passengerListforFlight.contains(id))
        {
            passengerListforFlight.remove(new Integer(id));
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

//-----------------------------------------------------------------------------------------------------

class Passenger{
    int id;
    ArrayList<Integer> myFlights;
    yoLock lock;

    public Passenger(int id){
        this.id=id;
        this.myFlights = new ArrayList<Integer>();
        lock=new yoLock();
    }

    public void cancelFromPassenger(int F){
        if(myFlights.contains(F)==false)
            System.out.println("You didn't book on this flight.");
        else this.myFlights.remove(new Integer(F));
    }

    public void reserveInPassenger(int F){
        this.myFlights.add(F);
    }

    public int getId(){
        return this.id;
    }

    public ArrayList<Integer> getMyFlights(){
        return this.myFlights;
    }
}

//----------------------Flight Database-----------------------------------------------------------------------

class flightDatabase{
    private Flights[] flightList;										//database of flights and data related to it.
    private yoLock lock;                                                //For lock on entire flight database

    public flightDatabase(int noOfFlights,HashMap lockTable){
        flightList= new Flights[noOfFlights+1];
        lock=new yoLock();
        Random r =new Random();
        for(int i=0;i<noOfFlights+1;i++){
            flightList[i]=new Flights(5+r.nextInt(11),i);   //initialize flights with random no of seats
            lockTable.put((Object) flightList[i],-1);          //initially items are not locked (value=-1)
        }
    }

    public yoLock getLock(){ return lock; }
    public Flights[] getFlightList() {
        return flightList;
    }
    public void setFlightList(Flights[] flightList) {
        this.flightList = flightList;
    }
}
//----------------Passenger Database--------------------------------------------------------------------------

class passengerDatabase{
    Passenger[] passengerListforDatabase;						//database of all passenger on all flights.
    yoLock lock;                                                //For lock on entire passenger database

    public passengerDatabase(){
        lock=new yoLock();
        passengerListforDatabase=new Passenger[10000];
    }
    public Passenger[] getPassengerList() {
        return passengerListforDatabase;
    }

    public void setPassengerList(Passenger[] passengerListforDatabase) {
        this.passengerListforDatabase = passengerListforDatabase;
    }
}
//===========================================================================================

public class Airplanes2PL {
    public static void main(String[] args) throws InterruptedException{
        //*******************************************************
        HashMap<Object,Integer> lockTable=new HashMap<Object,Integer>();
        //*******************************************************

        flightDatabase fDataBase=new flightDatabase(5,lockTable);
        passengerDatabase pDataBase=new passengerDatabase();

        lockTable.put((Object) fDataBase, -1);          //put flight database in hashmap, won't be used in v2
        lockTable.put((Object) pDataBase, -1);          //put passenger database in hashmap, won't be used in v2
//        int temp=lockTable.get((Object)fDataBase);
        //  System.out.println(lockTable.get((Object)fDataBase));



        //Thread[] threads=new Thread[30];

        //Transaction2PL[] transaction=new Transaction2PL[30];


        ExecutorService pool= Executors.newFixedThreadPool(100);
        long timerStart=System.currentTimeMillis();
        for(int i=0;i<100;i++) {
            Transaction2PL transaction=new Transaction2PL(fDataBase,pDataBase,lockTable);
            pool.execute(transaction);
        }
        pool.shutdown();
        long timerEnd=System.currentTimeMillis();
        while (!pool.isTerminated())
            timerEnd=System.currentTimeMillis();
        System.out.println("Time elapsed="+(timerEnd-timerStart));

    }
}
