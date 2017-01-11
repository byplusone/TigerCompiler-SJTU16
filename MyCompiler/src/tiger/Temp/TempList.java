package tiger.Temp;

public class TempList {
   public Temp head ;
   public TempList tail;
   public TempList(Temp h, TempList t) {head=h; tail=t;}

   public static int size(TempList list){
      int s = 0;
      TempList temp = list;
      while(temp != null){
         temp = list.tail;
         s++;
      }
      return s;
   }
   //public int size = size();

   public TempList(TempList h, TempList t)
   {
//      this.head = h.head;
//      this.tail = t;
      TempList temp=null;
      while (h!=null)
      {
         temp=new TempList(h.head,temp);
         h=h.tail;
      }
      tail=t;
      while (temp!=null)
      {
         tail=new TempList(temp.head,tail);
         temp=temp.tail;
      }
      head=tail.head;
      tail=tail.tail;
   }
}

