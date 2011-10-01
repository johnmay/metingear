
/**
 * SerializationBenchmark.java
 *
 * 2011.09.12
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.benchmark;

import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


/**
 *          SerializationBenchmark – 2011.09.12 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class SerializationBenchmark {

    transient private static final Logger LOGGER = Logger.getLogger(SerializationBenchmark.class);


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File f = File.createTempFile("benchmark", ".xjava");
        ObjectOutput out = new ObjectOutputStream(new FileOutputStream(f));
        HighLevelContainer hlc = new HighLevelContainer();
        hlc.addResult(new Result("Result-1", null));
        hlc.addResult(new Result("Result-2", null));
        hlc.addResult(new Result("Result-3", null));
        ResultAggregator ra = new ResultAggregator();
        ra.addResult(new Result("Result-4", ra));
        ra.addResult(new Result("Result-5", ra));
        ra.addResult(new Result("Result-6", ra));
        hlc.addAggregator(ra);
        out.writeObject(hlc);
        out.close();
        System.out.println("file:" + f);
        System.out.println("file size:" + f.length()); // 504 norm serialization


//        ObjectInput in = new ObjectInputStream(new FileInputStream(f));
//        HighLevelContainer hlc2 = new HighLevelContainer();
//        hlc2.readExternal(in);
//        in.close();

    }


}


class HighLevelContainer implements Externalizable {

    //   private static final long serialVersionUID = 3L;
    private List<Result> allResults = new ArrayList<Result>();
    private List<ResultAggregator> aggregators = new ArrayList<ResultAggregator>();


    public HighLevelContainer() {
    }


    public void addAggregator(ResultAggregator agg) {
        aggregators.add(agg);
        for( Result r : agg.getResults() ) {
            addResult(r);
        }
    }


    public void addResult(Result res) {
        allResults.add(res);
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(aggregators.size());
        for( ResultAggregator ag : aggregators ) {
            ag.writeExternal(out);
        }

        out.write(allResults.size());
        for( Result r : allResults ) {
            r.writeExternal(out);
        }
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int nAggs = in.readInt();
        for( int i = 0 ; i < in.read() ; i++ ) {
            ResultAggregator ra = new ResultAggregator();
            ra.readExternal(in);
            aggregators.add(ra);
        }
        int nRes = in.read();
        for( int i = 0 ; i < nRes ; i++ ) {
            Result r = new Result();
            r.readExternal(in);
            allResults.add(r);
        }
    }


}

