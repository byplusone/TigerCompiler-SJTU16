package tiger.Translate;

public class ProcFrag extends Frag {
  public tiger.Tree.Stm body;
  public tiger.Frame.Frame frame;
  public ProcFrag(tiger.Tree.Stm b, tiger.Frame.Frame f) {
    body = b;
    frame = f;
  }
}
