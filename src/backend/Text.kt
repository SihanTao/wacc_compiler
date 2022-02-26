package backend;

public class Text implements Directive {
    @Override
    public String toString() {
        return "\t.text\n";
    }
}
