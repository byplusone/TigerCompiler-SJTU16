package tiger.Types;

public class NAME extends Type {
   public tiger.Symbol.Symbol name; //类型名称
   private Type binding;   //绑定的实际类型
   public NAME(tiger.Symbol.Symbol n) {name=n;}

   //检测循环定义
   public boolean isLoop() {
      Type b = binding; 
      boolean any;
      binding=null;  //先把 binding 去掉并临时保存
      if (b==null) any=true;  //如果下一次出现了以上状态则出现循环引用
      else if (b instanceof NAME)   //如果还是 NAME 类型,递归继续
            any=((NAME)b).isLoop();
      else any=false;   //否则递归结束,没有循环定义
      binding=b;  //测试完毕,恢复
      return any;
     }
     
   public Type actual() {   //返回实际类型
      return binding.actual();   //其它类型的 actual 函数返回真正的类型
   }

   public boolean coerceTo(Type t) {
	return this.actual().coerceTo(t);
   }
   public void bind(Type t) {binding = t;}   //绑定实际类型

}
