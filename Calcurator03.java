import java.io.*;
import java.util.*;

//追加した項目　
//!と単項演算子に対応
//順列とコンビネーション


// トークンを表す抽象クラス
// トークン間の比較のために Comparable インタフェースを実装
abstract class Token implements Comparable<Token> {
    // トークンの文字列表現
    private String string;
    // トークンの種類
    protected int kind;
    // トークンの種類を表す定数
    // 値は演算子順位行列の添字と対応していれば何でもよく、順不同
    public static final int START = 0;  // 始
    public static final int SUM = 1;    // +
    public static final int SUB = 2;    // -
    public static final int MUL = 3;    // *
    public static final int DIV = 4;    // /
    public static final int MOD = 5;    // %
    public static final int factor = 6; // !
    public static final int Conbination = 7; //@
    public static final int Permutation = 8; //順列 ¥
    public static final int LPAREN = 9; // (
    public static final int RPAREN = 10; // )
    public static final int OPERAND = 11; // 識別子(変数)または定数
    public static final int END = 12;    // 終
    // 演算子順位行列
    // トークンを追加する場合には、対応する行と列の両方を追加
    private static final int order[][] = { // [左][右]
            // 始  +  -  *  /  %  !  @  ¥  (  ) 識別子 終 <-右  左
            {   9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 1,   0 }, // 始
            {   9,-1,-1, 1, 1, 1, 1, 1, 1, 1,-1, 1,  -1 }, // +
            {   9,-1,-1, 1, 1, 1, 1, 1, 1, 1,-1, 1,  -1 }, // -
            {   9,-1,-1,-1,-1,-1,-1,-1,-1, 1,-1, 1,  -1 }, // *
            {   9,-1,-1,-1,-1,-1,-1,-1,-1, 1,-1, 1,  -1 }, // /
            {   9,-1,-1,-1,-1,-1,-1,-1,-1, 1,-1, 1,  -1 }, // %
            {   9,-1,-1,-1,-1,-1,-1,-1,-1, 1,-1, 1,  -1 }, // !
            {   9,-1,-1,-1,-1,-1,-1,-1,-1, 1,-1, 1,  -1 }, // @
            {   9,-1,-1,-1,-1,-1,-1,-1,-1, 1,-1, 1,  -1 }, // ¥
            {   9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,   9 }, // (
            {   9, 9, 9, 9, 9, 9, 9, 1, 1, 9, 9, 9,   9 }, // )
            {   9,-1,-1,-1,-1,-1,-1, 1, 1, 9,-1, 9,  -1 }, // 識別子
            {   9, 9, 9, 9, 9, 9, 9, 1, 1, 9, 9, 9,   9 }, // 終
    };
    // コンストラクタ
    public Token(String string) {
        this.string = string;
    }
    public String toString() {
        return string;
    }
    public int getKind() {
        return kind;
    }
    // トークン間の比較
    public int compareTo(Token anotherToken) {
        return order[this.kind][anotherToken.getKind()];
    }
}


// Token のサブクラスのインスタンスを生成するための Factory クラス
class TokenFactory {
    public static Token newInstance(String string) {
        Token instance;
        if(string.equals("`") || string.equals(";") || string.equals("="))
            instance = new SpecialSymbol(string);
        else if(string.equals("+"))
            instance = new Sum(string);
        else if(string.equals("-"))
            instance = new Sub(string);
        else if(string.equals("*"))
            instance = new Mul(string);
        else if(string.equals("/"))
            instance = new Div(string);
        else if(string.equals("("))
            instance = new LParen(string);
        else if(string.equals(")"))
            instance = new RParen(string);
        else if(string.equals("%"))
            instance = new Mod(string);
        else if (string.equals("!"))
            instance = new factor(string);
        else if(string.equals("@"))
            instance = new Conbination(string);
        else if (string.equals("¥"))
            instance = new Permutation(string);
        else if(string.matches("\\d+"))  // 数字の並びは整数定数
            instance = new IntConstant(string);
        else // 識別子(変数)
            instance = new Identifier(string);
        return instance;
    }
}


// 特殊記号 ` ; =
class SpecialSymbol extends Token {
    public SpecialSymbol(String string) {
        super(string);
        if     (string.equals("`")) kind = START;
        else if(string.equals(";")) kind = END;
        // "=" は扱わないので何もしない
    }
}

// 2項演算子の抽象クラス。2引数の calc メソッドを持つ
abstract class BinaryOperator extends Token {
    public BinaryOperator(String string) {
        super(string);
    }
    public abstract int calc(int operand1, int operand2);
}

abstract class UnaryOperator extends Token{
    public UnaryOperator(String string){
        super(string);
    }
    public abstract int calc(int operand1);
}
// 2項演算子 +
class Sum extends BinaryOperator {
    public Sum(String string) {
        super(string);
        kind = SUM;
    }
    public int calc(int operand1, int operand2) {
        return operand1 + operand2;
    }
}

// 2項演算子 -
class Sub extends BinaryOperator {
    public Sub(String string) {
        super(string);
        kind = SUB;
    }
    public int calc(int operand1, int operand2) {
        return operand1 - operand2;
    }
}

// 2項演算子 *
class Mul extends BinaryOperator {
    public Mul(String string) {
        super(string);
        kind = MUL;
    }
    public int calc(int operand1, int operand2) {
        return operand1 * operand2;
    }
}

// 2項演算子 /
class Div extends BinaryOperator {
    public Div(String string) {
        super(string);
        kind = DIV;
    }
    public int calc(int operand1, int operand2) {
        return operand1 / operand2;
    }
}

class Mod extends BinaryOperator{
    public Mod(String string){
        super(string);
        kind = MOD;
    }
    public int calc(int operand1, int operand2){
        return  operand1 % operand2;
    }
}

class factor extends UnaryOperator{
    public factor(String string){
        super(string);
        kind = factor;
    }
    public  int calc(int operand){
        return Factorial.factorial(operand);
    }
}

class Factorial {
    static int result = 1;

    public static int factorial(int x) {
        if (x == 1) {
            return result;
        } else if (x > 1) {
            return x * factorial(x - 1);
        } else if (x == 0) {
            return result;
        }
        return result;
    }
}


class Conbination extends BinaryOperator{
    public Conbination(String string){
        super(string);
        kind = Conbination;
    }
    public int calc(int operand1, int operand2){
        return Factorial.factorial(operand1) / (Factorial.factorial(operand2) * Factorial.factorial(operand1 - operand2));
    }
}

class Permutation extends BinaryOperator{
    public Permutation(String string){
        super(string);
        kind = Permutation;
    }
    public int calc(int operand1, int operand2){
        return Factorial.factorial(operand1) / Factorial.factorial(operand2);
    }
}

// 左括弧
class LParen extends Token {
    public LParen(String string) {
        super(string);
        kind = LPAREN;
    }
}

// 右括弧
class RParen extends Token {
    public RParen(String string) {
        super(string);
        kind = RPAREN;
    }
}

// 整数定数
class IntConstant extends Token {
    private int value;
    public IntConstant(String string) {
        super(string);
        try {
            value = Integer.parseInt(string);
        }
        catch(Exception e) {
            System.err.println(e);
        }
        kind = OPERAND;
    }
    public int intValue() {
        return value;
    }
}

// 識別子(変数名)
class Identifier extends Token {
    public Identifier(String string) {
        super(string);
        kind = OPERAND;
    }
}


// プログラム本体
class Calcurator {
    // 変数の名前と値の組を格納する HashMap (値は整数限定)
    private HashMap<String, Integer> symbolMap;
    public Calcurator() {
        symbolMap = new HashMap<String, Integer>();

        System.out.println("式の値を求めます (Ctrl-d で終了)");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        while(true) {
            System.out.print("代入文を入力してください: ");
            String line = null;
            try {
                line = reader.readLine();
            }
            catch(IOException e) {
                break;
            }
            if(line == null) // 入力終了 (Ctrl-d)
                break;
            // 文字列 line を Token に分解しリストに
            LinkedList<Token> inputList = stringToTokenList(line);
            // 代入文を仮定しているので先頭は左辺の変数
            String variableName = inputList.poll().toString();
            // 次の "=" を取り除いておく
            inputList.poll();
            // 右辺 ("=" の次) から逆ポーランド記法に変換
            LinkedList<Token> reversePolishList = reverse(inputList);
            // 逆ポーランド記法に変換した結果を表示
            System.out.print("逆ポーランド記法による表現: ");
            for(Token token : reversePolishList)
                System.out.print(token.toString() + " ");
            System.out.println();
            // 逆ポーランド記法の式の値を計算
            Integer value = calculate(reversePolishList);
            // 変数に代入
            symbolMap.put(variableName, value);
            System.out.println(" " + variableName + " <- " + value);
        }
    }
    // 入力された文字列からトークンのリストを生成
    private LinkedList<Token> stringToTokenList(String string) {
        LinkedList<Token> inputList = new LinkedList<Token>();
        StringTokenizer st = new StringTokenizer(string, " `;=()+-*/%!@¥", true);
        while (st.hasMoreTokens()) {
            String tokenString = st.nextToken();
            if(!tokenString.equals(" "))
                inputList.add(TokenFactory.newInstance(tokenString));
        }
        return inputList;
    }
    // 入力されたトークンのリストから逆ポーランド記法のリストを生成
    private LinkedList<Token> reverse(LinkedList<Token> inputList) {
        LinkedList<Token> reversePolishList = new LinkedList<Token>();
        inputList.add(TokenFactory.newInstance(";")); // 終了記号
        Stack<Token> tokenStack = new Stack<Token>();
        tokenStack.push(TokenFactory.newInstance("`")); // 開始記号
        do {
            System.out.println("stack: " + tokenStack);
            Token a = inputList.poll();  // 先頭を取り出す
            if(a.getKind() == Token.OPERAND) {
                // オペランド(定数か変数)だったらすぐに出力
                reversePolishList.add(a);
                System.out.println(" 出力: " + reversePolishList);
            }
            else {
                Token s = tokenStack.peek();
                while(s.compareTo(a) == -1) {
                    System.out.println(" 比較: \"" + s + "\" > \"" + a + "\"");
                    // a よりも優先度の高い演算子がスタックにあったら出力
                    reversePolishList.add(tokenStack.pop());
                    System.out.println("stack: " + tokenStack);
                    System.out.println(" 出力: " + reversePolishList);
                    s = tokenStack.peek();
                }
                if(s.compareTo(a) == 1) {
                    System.out.println(" 比較: \"" + s + "\" < \"" + a + "\"");
                    // a より優先度の低い演算子がスタックの先頭だったら
                    // a をスタックに積む
                    tokenStack.push(a);
                    System.out.println("stack: " + tokenStack);
                }
                else if(s.compareTo(a) == 0) {
                    System.out.println(" 比較: \"" + s + "\" == \"" + a + "\"");
                    tokenStack.pop(); // 取り出して捨てる
                    System.out.println("stack: " + tokenStack);
                }
            }
        } while(!tokenStack.empty());
        return reversePolishList;
    }
    // 逆ポーランド記法のトークンリストを演算
    private Integer calculate(List<Token> reversePolishList) {
        // スタックを用意 (とりあえず中身はInteger)
        // 中身は Integer だがオートボクシングにより int のように使える
        Stack<Integer> stack = new Stack<Integer>();
        for(Token token : reversePolishList) {
            // 現在のスタックを表示
            System.out.print(stack);
            // 2項演算子だったら、前に数値が2つ push されているはずなので、
            // それらを pop して演算し、結果の値を push する
            if(token instanceof BinaryOperator) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                System.out.println(" 演算: " + operand1 + ", " + operand2);
                stack.push(((BinaryOperator)token).calc(operand1, operand2));
            }
            else if (token instanceof UnaryOperator){
                int operand = stack.pop();
                System.out.println("演算: " + operand);
                stack.push(((UnaryOperator)token).calc(operand));
            }
            else if(token instanceof IntConstant) { // 数値
                System.out.println(" 値を push: " + token.toString());
                // 文字列から数値に変換して push
                stack.push(((IntConstant)token).intValue());
            }
            else { // 演算子でも数値でもないものは識別子
                System.out.println(" 識別子: " + token.toString());
                // 値を HashMap から求めて push
                if(symbolMap.containsKey(token.toString())) {
                    Integer value = symbolMap.get(token.toString());
                    stack.push(value);
                }
                else {
                    System.err.println(" 未定義: " + token);
                }
            }
        }
        // 最終的に stack には計算された値1つが入っている
        return stack.pop();
    }
}

public class Calcurator03 {
    public static void main(String[] args) {
        Calcurator calcurator = new Calcurator();
    }
}