package tiger.Translate;

public class Access {
  Level home;
  public tiger.Frame.Access acc;
  public Access(Level h, tiger.Frame.Access a) {
    home = h;
    acc = a;
  }
  public String toString() {
    return "[" + home.frame.name.toString() + "," + acc.toString() + "]";
  }
}
