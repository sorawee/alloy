/* Alloy Analyzer 4 -- Copyright (c) 2006-2009, Felix Chang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.*;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;


public class AlloyRunner {
    /*
     * Usage: AlloyRunner <file.als> num-instances
     */

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Error: wrong parameter");
            System.exit(1);
        }

        String filename = args[0];
        System.out.println("=========== Parsing+Typechecking "+filename+" =============");

        A4Reporter rep = new A4Reporter();
        Module world = CompUtil.parseEverything_fromFile(rep, null, filename);
        A4Options options = new A4Options();
        options.noOverflow = true;
        options.seed = Integer.parseInt(args[2]);
        options.solver = A4Options.SatSolver.SAT4J;

        int maxinstances = Integer.parseInt(args[1]);
        for (Command command: world.getAllCommands()) {
            System.out.println("============ Command "+command+": ============");
            A4Solution ans = call(null, rep, world, command, options, 1);
            if (!ans.satisfiable()) {
                continue;
            }
            for(int trial = 2; trial <= maxinstances; trial++) {
                ans = call(ans, null, null, null, null, trial);
                if (!ans.satisfiable()) break;
            }
        }
    }

    static private A4Solution call(A4Solution old, A4Reporter rep, Module world, Command command, A4Options options, int trial) throws Exception {
        System.out.println("============= Instance " + trial + " =============");
        A4Solution ans;
        if (old == null) {
            ans = TranslateAlloyToKodkod.execute_command(
                rep, world.getAllReachableSigs(), command, options);
        } else {
            ans = old.next();
        }
        System.out.println(ans);
        return ans;
    }
}
