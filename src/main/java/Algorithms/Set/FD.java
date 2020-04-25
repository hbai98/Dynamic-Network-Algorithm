package Algorithms.Set;

import org.jgrapht.alg.util.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a class for MFD(minimum functional dependencies)
 */
public class FD {
    private String regex = "\\s*[a-zA-Z]+\\s*->\\s*[a-zA-Z]+\\s*";
    private HashSet<Pair<String, String>> functions;

    FD(String s) throws Exception {
        checkPattern(s);
    }

    public void run() {
        splitRight();
        removeLeft();
        removeRedundant();
    }

    private void checkPattern(String s) throws Exception {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(s);
        if (!matcher.find()) {
            throw new Exception("Your input is not valid.");
        }
        functions = new HashSet<>();
        do {
            String function = matcher.group();
            int i = 0;
            StringBuilder buf = new StringBuilder();
            ArrayList<String> list = new ArrayList<>();
            while (i < function.length()) {
                char charF = function.charAt(i);

                if (Character.isWhitespace(charF)) {
                    i++;
                    continue;
                }
                if (Character.isLetter(charF)) {
                    buf.append(charF);
                    i++;
                    continue;
                }
                if (charF == '-') {
                    list.add(buf.toString());
                    buf = new StringBuilder();
                }
                i++;
            }
            list.add(buf.toString());
            functions.add(new Pair<>(list.get(0), list.get(1)));
        } while (matcher.find());
    }

    public void splitRight() {
        HashSet<Pair<String, String>> functionsR = new HashSet<>();
        functions.forEach(pair -> {
            String right = pair.getSecond();
            while (right.length() != 1) {
                functionsR.add(new Pair<>(pair.getFirst(), right.substring(0, 1)));
                right = right.substring(1);
            }
            functionsR.add(new Pair<>(pair.getFirst(), right));
        });
        functions = functionsR;
    }


    public void removeRedundant() {
        for(Iterator<Pair<String,String>> i = functions.iterator();i.hasNext();){
            Pair<String,String> pair = i.next();
            ArrayList<Character> toCheck = stringToCharList(pair.getSecond());
            HashSet<Character> attributeSet = getAttributesSetExceptFunction(pair);
            if (attributeSet.containsAll(toCheck)) {
                // have to update the function hashset immediately after the redundancy has happened.
                // but during the iteration it will throw java.util.ConcurrentModificationException.
                // So I change it to iterator instead of using set.remove()
                // like https://stackoverflow.com/questions/1110404/remove-elements-from-a-hashset-while-iterating

                // functions.remove(pair);
                i.remove();
            }
        }


    }

    private ArrayList<Character> stringToCharList(String s) {
        char[] s1 = s.toCharArray();
        ArrayList<Character> toCheck = new ArrayList<>();
        for (char c : s1) {
            toCheck.add(c);
        }
        return toCheck;
    }

    public HashSet<Character> getAttributesSetExceptFunction(Pair<String, String> function) {
        HashSet<Character> set = new HashSet<>();
        char[] s1 = function.getFirst().toCharArray();
        for (char value : s1) {
            set.add(value);
        }
        // Notice :  if the property set has changed, the iteration should be implemented again!
        while (true) {
            boolean changed = false;
            for (Pair<String, String> pair : functions) {
                if (!pair.equals(function)) {
                    ArrayList<Character> pre = stringToCharList(pair.getFirst());
                    ArrayList<Character> res = stringToCharList(pair.getSecond());
                    if (set.containsAll(pre)) {
                        if (set.addAll(res)) {
                            //can iterate starting form the head now.
                            changed = true;
                            break;
                        }
                    }
                }
            }
            if (!changed) {
                break;
            }
        }
        return set;
    }

    private HashSet<Character> getAttributesSet(String attribute) {
        // empty
        if (attribute.length() == 0) {
            return new HashSet<>();
        }
        ArrayList<Character> attributes = stringToCharList(attribute);
        HashSet<Character> set = new HashSet<>(attributes);
        // Notice :  if the property set has changed, the iteration should be implemented again!
        while (true) {
            boolean changed = false;
            for (Pair<String, String> pair : functions) {
                ArrayList<Character> pre = stringToCharList(pair.getFirst());
                ArrayList<Character> res = stringToCharList(pair.getSecond());
                if (set.containsAll(pre)) {
                    if (set.addAll(res)) {
                        changed = true;
                    }
                }
            }
            if (!changed) {
                break;
            }
        }
        return set;
    }


    public void removeLeft() {
        // HashSet no concurrent change
        HashSet<Pair<String, String>> functionsR = new HashSet<>();
        for (Pair<String, String> function : functions) {
            String first = function.getFirst();
            String second = function.getSecond();
            ArrayList<Character> res = stringToCharList(second);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < first.length(); i++) {
                String s = first.substring(i, i + 1);
                // replace all c to deal with redundant head like AAB -》 xxx
                String leftAtr = first.replace(s, "");
                //TODO dynamic programming to save attribute set
                HashSet<Character> attributeSet = getAttributesSet(leftAtr);
                // save
                if (!attributeSet.containsAll(res)) {
                    // record real first
                    builder.append(first.charAt(i));
                }
            }
            // add to result
            functionsR.add(new Pair<>(builder.toString(), second));
        }
        functions = functionsR;
    }

    public static void main(String[] args) throws Exception {
        FD fd = new FD("B->D，A->D，DA->CB，CD->AB->D，A->D，DA->CB，CD->A");
        fd.run();
    }
    public void setRegex(String regex) {
        this.regex = regex;
    }

    public HashSet<Pair<String, String>> getFunctions() {
        return functions;
    }

}
