package tiger.Translate;
import tiger.Temp.Label;
import tiger.Symbol.Symbol;
import tiger.Util.BoolList;

public class Level {
  public Level parent;  //直接上级层
  public tiger.Frame.Frame frame; //帧
  public AccessList formals;  //参数（第一个为静态连接，后面为其他参数）

  public Level(Level parent, Symbol name, BoolList fmls) {
    this.parent = parent;
    this.frame = parent.frame.newFrame(new Label(name), new BoolList(true, fmls));
    for (tiger.Frame.AccessList f = frame.formals; f != null; f = f.tail)
      this.formals = new AccessList(new Access(this, f.head), this.formals);
  }

  public Access staticLink() {return formals.head;}

  public Level(tiger.Frame.Frame f) {
    frame = f;
  }

  public Label name() {
    return frame.name;
  }

  public Access allocLocal(boolean escape) {
    return new Access(this, frame.allocLocal(escape));
  }
}
