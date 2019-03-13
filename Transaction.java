import java.util.ArrayList;
import java.util.Random;

public class Transaction implements Runnable{
	private Database obj;
	private ConcurrencyControlManager CCM;

	public Transaction(Database obj,ConcurrencyControlManager CCM) {
		this.obj=obj;
		this.CCM=CCM;
	}

//---------------------------------------------------------------------------------------

	public void reserve(int F,int id){
    	CCM.acquireExclusiveLock();										//Acquire exclusive lock since database being modified
    	  try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
    	if(obj.getFlightList()[F].getFreeSeats()>0){
        if(obj.getPassengerList()[id]==null) obj.getPassengerList()[id]=new Passenger(id);	//if user does not already exist, create an account for him/her
        obj.getPassengerList()[id].reserveInPassenger(F);
        obj.getFlightList()[F].reserveInFlights(id);
        System.out.println("Reserved seat on flight "+F+" for passenger with ID: "+id);
        }
        else System.out.println("Failed to reserve seat, no seat free in this flight");

        CCM.releaseExclusiveLock();
    }

//-----------------------------------------------------------------------------------------

	public void cancel(int F,int id){
    	CCM.acquireExclusiveLock();										//Acquire exclusive lock since database being modified
    	  try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
    	if(obj.getPassengerList()[id]==null) System.out.println("No passenger with this id exists."+Thread.currentThread().getName());
        else{
        	obj.getPassengerList()[id].cancelFromPassenger(F);
        	obj.getFlightList()[F].cancelFromFlight(id);
        }
        CCM.releaseExclusiveLock();
    }

//------------------------------------------------------------------------------------------
 
	public ArrayList<Integer> my_Flights(int id){
    	CCM.acquireExclusiveLock();										//Acquire exclusive lock
    	  try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
        if(obj.getPassengerList()[id]==null){
            System.out.println("No such passenger exists ");
            CCM.releaseExclusiveLock();
            return null;
        }
        else {
        	System.out.println("Found passenger.");
        	CCM.releaseExclusiveLock();
        	return obj.getPassengerList()[id].getMyFlights();
        }
    }

//------------------------------------------------------------------------------------------

	public int total_Reservations(){
		CCM.acquireExclusiveLock();										//Acquire exclusive lock
		  try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
        int totalReservationsCount=0;
        for(int i=0;i<obj.getFlightList().length;i++) totalReservationsCount+=obj.getFlightList()[i].getBookedSeats();

        CCM.releaseExclusiveLock();

        return totalReservationsCount;
    }

//------------------------------------------------------------------------------------------

	public void transfer(int F1,int F2,int id){
		CCM.acquireExclusiveLock();										//Acquire exclusive lock since database being modified
		  try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
    	boolean foundInF1=false;
        boolean roomInF2 =false;
        if(obj.getFlightList()[F1].getPassengerListforFlight().contains(id)){	//if passenger has booking on flight F1
                System.out.println("Found your booking on flight "+F1);
            	foundInF1=true;
        }
        else System.out.println("Passenger does not have booking on this flight.");

        if(obj.getFlightList()[F2].getFreeSeats()>0)	{
        	System.out.println("There are free seats on flight "+F2);
        	roomInF2=true;												//if there are free seats on flight F2
        }
        else System.out.println("Sorry! No free seats on this flight.");

        if(foundInF1 && roomInF2){										//Both conditions need to satisfy.
            //cancel passenger's seat from F1
            obj.getPassengerList()[id].cancelFromPassenger(F1);
            obj.getFlightList()[F1].cancelFromFlight(id);
            
            //reserve in F2
            obj.getPassengerList()[id].reserveInPassenger(F2);
            obj.getFlightList()[F2].reserveInFlights(id);

            System.out.println("Reserved seat on new flight.");
        }
        CCM.releaseExclusiveLock();
    }

//------------------------------------------------------------------------------------------    

	@Override
	public void run() {
		Random r=new Random();
		int choice=r.nextInt(5);
		int id=r.nextInt(5)+1;
		int F=r.nextInt(5)+1;
		int F2=F;
		while(F2!=F) F2=r.nextInt(5)+1;

		switch(choice) {
		case 0: reserve(F,id);
//				System.out.println(total_Reservations());
				break;

		case 1:	cancel(F,id);
				break;

		case 2: ArrayList<Integer> res=my_Flights(id);
				if(res!=null) {
					System.out.print("Res list : ");
					for(int i=0;i<res.size();i++) System.out.println(res.get(i));
					System.out.println();
				}
				else System.out.println("You don't have reservation on any flight");
				
				break;

		case 3: System.out.println(total_Reservations());
				break;

		case 4:	transfer(F,F2,id);
				break;
		}
	}
}