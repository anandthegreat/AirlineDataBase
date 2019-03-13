import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Transaction2PL implements Runnable{
    flightDatabase fObj;
    passengerDatabase pObj;
    HashMap<Object,Integer> lockTable;

    public Transaction2PL(flightDatabase fObj, passengerDatabase pObj, HashMap lockTable) {
        this.fObj=fObj;
        this.pObj=pObj;
        this.lockTable=lockTable;
    }

//---------------------------------------------------------------------------------------

    public void reserve(int F,int id){
//        fObj.getLock().xLock(lockTable,(Object)fObj);                              //acquire lock on flight database
        fObj.getFlightList()[F].lock.xLock(lockTable,(Object)fObj.getFlightList()[F]); //acquire lock on flight F

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(fObj.getFlightList()[F].getFreeSeats()>0){
//            pObj.lock.xLock(lockTable,(Object)pObj);                              //acquire lock on passenger database
            if(pObj.getPassengerList()[id]==null) {
                pObj.getPassengerList()[id]=new Passenger(id);	//if user does not already exist, create an account for him/her
                lockTable.put((Object) pObj.getPassengerList()[id],-1);
            }
            pObj.getPassengerList()[id].lock.xLock(lockTable,(Object)pObj.getPassengerList()[id]);       //acquire lock on passenger "id"
            //***************************************************
            pObj.getPassengerList()[id].reserveInPassenger(F);
            fObj.getFlightList()[F].reserveInFlights(id);
            System.out.println("Reserved seat on flight "+F+" for passenger with ID: "+id);
            //***************************************************
            pObj.getPassengerList()[id].lock.xUnlock(lockTable,(Object)pObj.getPassengerList()[id]);     //release lock on passenger "id"
//            pObj.lock.xUnlock(lockTable,(Object)pObj);                            //release lock on passenger database

        }
        else System.out.println("Failed to reserve seat, no seat free in this flight");


        fObj.getFlightList()[F].lock.xUnlock(lockTable,(Object)fObj.getFlightList()[F]);             //release lock on flight F
//        fObj.getLock().xUnlock(lockTable,(Object) fObj);                                //release lock on flight database
    }

//-----------------------------------------------------------------------------------------

    public void cancel(int F,int id){
//        fObj.getLock().xLock(lockTable,(Object)fObj);                              //acquire lock on flight database
        fObj.getFlightList()[F].lock.xLock(lockTable,(Object)fObj.getFlightList()[F]);           //acquire lock on flight F
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        pObj.lock.xLock(lockTable,(Object)pObj);                              //acquire lock on passenger database
        if(pObj.getPassengerList()[id]==null)
            System.out.println("No passenger with this id exists");
        else{
            pObj.getPassengerList()[id].lock.xLock(lockTable,(Object)pObj.getPassengerList()[id]);       //acquire lock on passenger "id"
            //*********************************************************
            pObj.getPassengerList()[id].cancelFromPassenger(F);
            fObj.getFlightList()[F].cancelFromFlight(id);
            System.out.println("Flight "+F+" has been cancelled for passenger "+id);
            //*********************************************************
            pObj.getPassengerList()[id].lock.xUnlock(lockTable,(Object)pObj.getPassengerList()[id]);     //release lock on passenger "id"
        }

//        pObj.lock.xUnlock(lockTable,(Object)pObj);                                //release lock on passenger database
        fObj.getFlightList()[F].lock.xUnlock(lockTable,(Object)fObj.getFlightList()[F]);             //release lock on flight F
//        fObj.getLock().xUnlock(lockTable,(Object)fObj);                                //release lock on flight database
    }

//------------------------------------------------------------------------------------------

    public ArrayList<Integer> my_Flights(int id){
        ArrayList<Integer> temp=null;                                //we will return this list
//        pObj.lock.sLock(lockTable,(Object)pObj);                                  //acquire shared lock on passenger database
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(pObj.getPassengerList()[id]==null){
            System.out.println("No such passenger exists ");
        }
        else {
            pObj.getPassengerList()[id].lock.sLock(lockTable,(Object)pObj.getPassengerList()[id]);      //acquire shared lock on passenger
            //*************************************************
            System.out.println("Found passenger.");
            temp= pObj.getPassengerList()[id].getMyFlights();
            pObj.getPassengerList()[id].lock.sUnlock(lockTable,(Object)pObj.getPassengerList()[id]);    //release shared lock on passenger
            //*************************************************
        }
//        pObj.lock.sUnlock(lockTable,(Object)pObj);                               //release shared lock on passenger database
        return temp;
    }

//------------------------------------------------------------------------------------------

    public int total_Reservations(){
      int totalRes=0;

      Flights[] flightList=fObj.getFlightList();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for(int i=1;i<fObj.getFlightList().length;i++) {
            flightList[i].lock.sLock(lockTable,(Object)flightList[i]);     //acquire shared lock on flight database
            totalRes+=flightList[i].getBookedSeats();
        }

        for(int i=flightList.length-1;i>=0;i--){
            flightList[i].lock.sUnlock(lockTable,(Object)fObj);      //release shared lock on flight database
        }

      return totalRes;
    }

//------------------------------------------------------------------------------------------

    public void transfer(int F1,int F2,int id){
//        fObj.getLock().xLock(lockTable,(Object)fObj);                               //acquire lock on flight database
        if(F1!=F2) {
            fObj.getFlightList()[F1].lock.xLock(lockTable, (Object) fObj.getFlightList()[F1]);           //acquire lock on flight F1
            System.out.println("Lock acquired on F1");
            fObj.getFlightList()[F2].lock.xLock(lockTable, (Object) fObj.getFlightList()[F2]);           //acquire lock on flight F2
            System.out.println("Lock acquired on F2");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            boolean foundInF1 = false;
            boolean roomInF2 = false;
            if (fObj.getFlightList()[F1].getPassengerListforFlight().contains(id)) {    //if passenger has booking on flight F1
                System.out.println("Found your booking on flight " + F1);
                foundInF1 = true;

            }
            if (foundInF1 == false)
                System.out.println("Passenger does not have booking on this flight.");

            if (fObj.getFlightList()[F2].freeSeats > 0) {
                roomInF2 = true;                                                //if there are free seats on flight F2
                System.out.println("There are free seats on flight " + F2);
            } else System.out.println("Sorry! No free seats on this flight.");


            if (foundInF1 && roomInF2) {
//            pObj.lock.xLock(lockTable,(Object)pObj);                          //acquire lock on passenger database
                //cancel passenger's seat from F1
                pObj.getPassengerList()[id].cancelFromPassenger(F1);
                fObj.getFlightList()[F1].cancelFromFlight(id);
                //reserve in F2
                pObj.getPassengerList()[id].reserveInPassenger(F2);
                fObj.getFlightList()[F2].reserveInFlights(id);

                System.out.println("Successfully transferred seat from flight " + F1 + " to flight " + F2);
//            pObj.lock.xUnlock(lockTable,(Object)pObj);                        //release lock on passenger database

            }

            fObj.getFlightList()[F2].lock.xUnlock(lockTable, (Object) fObj.getFlightList()[F2]);           //release lock on flight F2
            fObj.getFlightList()[F1].lock.xUnlock(lockTable, (Object) fObj.getFlightList()[F1]);          //release lock on flight F1
//        fObj.getLock().xUnlock(lockTable,(Object)fObj);                              //release lock on flight database
        }
        else System.out.println("Two flights are same");
    }

//------------------------------------------------------------------------------------------    

    @Override
    public void run() {
        Random r=new Random();
        int choice=r.nextInt(5);
        int id=r.nextInt(5)+1;
        int F=r.nextInt(5)+1;
        int F2=F;
       // while(F2==F) F2=r.nextInt(5)+1;

        switch(choice) {
            case 0:
                    System.out.println("reserve by "+ Thread.currentThread().getName());
                    reserve(F,id);
                    System.out.println(total_Reservations());
                    break;

            case 1:
                    System.out.println("cancel by "+ Thread.currentThread().getName());
                    cancel(F,id);
                    break;

            case 2:
                   System.out.println("my_Flights by "+ Thread.currentThread().getName());
                    ArrayList<Integer> res=my_Flights(id);
                    if(res!=null){
                        System.out.print("My Flights are : ");
                        for(int i=0;i<res.size();i++) System.out.println(res.get(i));
                    }
                    else System.out.println("You don't have reservation on any flight");
                    break;

            case 3: System.out.println("total_Reservation by "+ Thread.currentThread().getName());
                    System.out.println(total_Reservations());
                    break;

            case 4:	System.out.println("transfer by "+ Thread.currentThread().getName());
                    transfer(F,F2,id);
                    break;
        }
    }
}