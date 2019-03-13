import java.util.HashMap;
import java.util.List;

public class yoLock {
    volatile int lockStatus=-1;

    public synchronized void sLock(HashMap<Object,Integer> lockTable,Object obj){

        if(lockTable.get(obj)==1){
            while(lockTable.get(obj)==1){
            }
         }
         lockTable.put(obj,0);
        //lockStatus = 0;
    }

    public void sUnlock(HashMap<Object,Integer> lockTable,Object obj){
          if(lockTable.get(obj)==0){
              lockTable.put(obj,-1);
          }
    }

    public synchronized void xLock(HashMap<Object,Integer> lockTable,Object obj){
          if(lockTable.get(obj)==0 || lockTable.get(obj)==1){
            while(lockTable.get(obj)==0 || lockTable.get(obj)==1){
            }
        }
        System.out.println("Exclusive Locking item."+obj+" "+Thread.currentThread().getName());

        lockTable.put(obj,1);           //acquired exclusive lock
        //lockStatus=1;
    }

    public void xUnlock(HashMap<Object,Integer> lockTable,Object obj){
        if(lockTable.get(obj)==1){
           lockTable.put(obj,-1);
        //if(lockStatus==1){
        //  lockStatus=-1;
            System.out.println("Unlocking exclusive by "+Thread.currentThread().getName());
        }
    }

}