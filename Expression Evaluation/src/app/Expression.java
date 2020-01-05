package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

    public static String delims = " \t*+-/()[]";

    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void //works for now
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
     
        StringTokenizer expression=new StringTokenizer(expr.trim(),delims);

        while(expression.hasMoreElements()) {
            String operand=expression.nextToken();

            Array ao=null;

            boolean isarr=false;


            Variable vo=null;
            if(Character.isLetter(operand.charAt(0))==true){

                int bracketcheck=expr.indexOf(operand)+operand.length();

                if(bracketcheck!=expr.length()) {

                    if(expr.charAt(bracketcheck)=='[') {
                        isarr=true;
                    }

                }

                if(isarr) {
                    ao=new Array(operand);
                    if(!arrays.contains(ao)) {
                        arrays.add(ao);
                    }
                }else if(!isarr) {
                    vo=new Variable(operand);

                    if(!vars.contains(vo)) {
                        vars.add(vo);
                    }

                }

            }

        }        

    }

    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
            throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
                continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
                arr = arrays.get(arri);
                arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }

    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
     
       

        Stack<Float> operands=new Stack<Float>();

        Stack<String> operator=new Stack<String>();

        float evalanswer=0;

        

        
        
        


            StringTokenizer eval=new StringTokenizer(expr,delims,true);
            
            
            Stack<Float> toperands=new Stack<Float>();

            Stack<String> toperator=new Stack<String>();

            while(eval.hasMoreElements()) {
                String a=eval.nextToken();
                
                if(a.equals("(")) { //recursion time 
                    int matches=1;
                    
                    int end=expr.indexOf(a);
                    
                    for(int i=1; matches>0; i++) {
                        char curr=expr.charAt(expr.indexOf(a)+i);
                        if(curr==')') {
                            matches--;
                        }
                        if(curr=='(') {
                            matches++;
                        }
                        end++;
                    }
                    toperands.push(evaluate(expr.substring(expr.indexOf(a)+1,end),vars,arrays));
                    expr=expr.substring(end+1);
                    eval=new StringTokenizer(expr,delims,true);
                }
                

                if( a.equals("+") || a.equals("-")) {
                    if(!toperator.isEmpty()) {
                        while(!toperator.isEmpty() && (toperator.peek().equals("*") || toperator.peek().equals("/"))  ) {

                            float answer=0;;
                            if(toperator.peek().equals("*")) {
                                toperator.pop();

                                float two=toperands.pop();

                                float one=toperands.pop();

                                answer=one*two;

                            }else if(toperator.peek().equals("/")) {
                                toperator.pop();

                                float two=toperands.pop();

                                float one=toperands.pop();


                                answer=one/two;
                            }

                            toperands.push(answer);





                        }




                    }

                    toperator.push(a);

                }else if(a.equals("*") || a.equals("/")){
                    toperator.push(a);
                }else{

                      if(a.equals("(") || a.equals(")") || a.equals("]")) {
                          continue;
                      }

                    if(Character.isAlphabetic(a.charAt(0)) ) {
                        
                        if(!a.contains("[")) {
                            Variable thing=new Variable(a);

                            if(vars.contains(thing)) {
                                float cast=1;
                                 int index=0;
                                for(int i=0; i<vars.size(); i++) {
                                    if(vars.get(i).equals(thing)) {
                                        break;
                                    }
                                    index++;
                                }

                                toperands.push(cast*vars.get(index).value);
                                continue;
                        }
                        }
                        

                        if(expr.charAt(expr.indexOf(a)+a.length())!='[' ) {
                        Variable thing=new Variable(a);

                        if(vars.contains(thing)) {
                            float cast=1;
                             int index=0;
                            for(int i=0; i<vars.size(); i++) {
                                if(vars.get(i).equals(thing)) {
                                    break;
                                }
                                index++;
                            }

                            toperands.push(cast*vars.get(index).value);
                        }
                        
                        }
                        else if(expr.charAt(expr.indexOf(a)+a.length())=='[' &&expr.indexOf(a)+a.length()<expr.length()) {

                            int bracketmatch=1;
                            
                            int ending=expr.indexOf('[');
                            
                            for(int i=1; bracketmatch>0; i++) {
                                char array=expr.charAt(expr.indexOf(expr.charAt(expr.indexOf(a)+a.length()))+i);
                                
                                if(array==']') {
                                    bracketmatch--;
                                }
                                if(array=='[') {
                                bracketmatch++;
                            }
                                ending++;
                        
                        }
                            float arrindex=evaluate(expr.substring(expr.indexOf('[')+1,ending),vars,arrays);
                            expr=expr.substring(ending+1);
                            Array target=new Array(a);
                            int place=0;
                            for(int s=0; s<arrays.size(); s++) {
                                if(arrays.get(s).equals(target)) {
                                    break;
                                }
                                place++;
                            }
                            float convert=1;
                            toperands.push(convert*arrays.get(place).values[(int)arrindex]);
                            eval=new StringTokenizer(expr,delims,true);
                        }

                    }
                    else {
                        toperands.push(Float.parseFloat(a));



                    }


                }
                


            }

            
            
            if(!
                    toperator.isEmpty()) {
            
                
            if(toperator.peek().equals("*")) {
                 toperator.pop();
                
                float check2=toperands.pop();
                
                float check1=toperands.pop();
                
                toperands.push(check1*check2);
                
            }else if(toperator.peek().equals("/")) {
                  toperator.pop();
                
                float check2=toperands.pop();
                
                float check1=toperands.pop();
                
                toperands.push(check1/check2);
            }
                
                
                
            }


            
               while(!toperands.isEmpty()) {
                   operands.push(toperands.pop());
               }

                while(!toperator.isEmpty()) {
                    operator.push(toperator.pop());
                }





            while(!operator.isEmpty()) {

                float answer=0;
                if(operator.peek().equals("-")) {
                    operator.pop();

                    float first=operands.pop();

                    float second=operands.pop();

                    answer=first-second;



                }else if(operator.peek().equals("+")) {
                    operator.pop(); 

                    float first=operands.pop();

                    float second=operands.pop();

                    answer=first+second;


                }else if(operator.peek().equals("/")) {
                    operator.pop(); 

                    float first=operands.pop();

                    float second=operands.pop();

                    answer=first/second;

                }else if(operator.peek().equals("*")) {
                    operator.pop(); 

                    float first=operands.pop();

                    float second=operands.pop();

                    answer=first*second;

                }
                operands.push(answer);
            }

            evalanswer=operands.pop();










        
        return evalanswer;
    }

}
