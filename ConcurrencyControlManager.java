import java.util.ArrayList;

public class ConcurrencyControlManager {
	private Database obj;
	private volatile static int databaseLockStatus=-1;
	private ArrayList<Thread> sharedLockAcquirers;

	public ConcurrencyControlManager(Database obj) {
		this.obj=obj;
		sharedLockAcquirers=new ArrayList<Thread>();
	}
	public synchronized void acquireExclusiveLock() {
//		System.out.println(Thread.currentThread().getName());
		if(databaseLockStatus==0 || databaseLockStatus==1) {			//0 is for shared, 1 is for exclusive
			while(databaseLockStatus!=-1){								//waiting empty loop
			//	System.out.print("Waiting for exclusive. ");
			//	System.out.println(Thread.currentThread().getName());
			}
			System.out.println("Lock now available. Locking."+Thread.currentThread().getName());
		}
		System.out.println("Exclusive Locking item."+Thread.currentThread().getName());
		databaseLockStatus=1;
	}

	public synchronized void acquireSharedLock(Thread t) {
		if(databaseLockStatus==1) {
			System.out.println("Waiting for shared. "+Thread.currentThread().getName());
			while(databaseLockStatus!=-1){}	//empty while loop for waiting.
			databaseLockStatus=0;
			System.out.println("Acquired shared lock after waiting."+Thread.currentThread().getName());
		}

		else {
			System.out.println("Acquired shared lock."+Thread.currentThread().getName());
			databaseLockStatus=0;
			sharedLockAcquirers.add(t);
			System.out.println();
			System.out.print("Currently these items have shared lock on database : ");
			for(int i=0;i<sharedLockAcquirers.size();i++) System.out.print(sharedLockAcquirers.get(i).getName()+" ");		//printing shared lock holders list
			System.out.println();
		}
	}

	public void releaseExclusiveLock() {
		System.out.println("Unlocking exclusive by "+Thread.currentThread().getName());
		databaseLockStatus=-1;
	}

	public void releaseSharedLock(Thread t) {
		System.out.println("Unlocking shared by "+Thread.currentThread().getName());
		sharedLockAcquirers.remove(t);
		databaseLockStatus=-1;
	}
}