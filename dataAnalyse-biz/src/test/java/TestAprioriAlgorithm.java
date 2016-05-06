import com.xugang.apriori.AprioriAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by XuGang on 2016/5/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/config/spring/*.xml")
public class TestAprioriAlgorithm {
    private AprioriAlgorithm apriori;
    private Map<Integer, Set<String>> txDatabase;
    private Float minSup = new Float("0.50");
    private Float minConf = new Float("0.70");
    @Before
    protected void setUp() throws Exception {
        create(); // 构造事务数据库
        apriori = new AprioriAlgorithm(txDatabase, minSup, minConf);
    }

    public void create() {
        txDatabase = new HashMap<Integer, Set<String>>();
        Set<String> set1 = new TreeSet<String>();
        set1.add("A");
        set1.add("B");
        set1.add("C");
        set1.add("E");
        txDatabase.put(1, set1);
        Set<String> set2 = new TreeSet<String>();
        set2.add("A");
        set2.add("B");
        set2.add("C");
        txDatabase.put(2, set2);
        Set<String> set3 = new TreeSet<String>();
        set3.add("C");
        set3.add("D");
        txDatabase.put(3, set3);
        Set<String> set4 = new TreeSet<String>();
        set4.add("A");
        set4.add("B");
        set4.add("E");
        txDatabase.put(4, set4);
    }

    @Test
    public void testFreq1ItemSet() {
        System.out.println("挖掘频繁1-项集 : " + apriori.getFreq1ItemSet());
    }

    @Test
    public void testAprioriGen() {
        System.out.println(
                "候选频繁2-项集 ： " +
                        this.apriori.aprioriGen(1, this.apriori.getFreq1ItemSet().keySet())
        );
    }

    @Test
    public void testGetFreq2ItemSet() {
        System.out.println(
                "挖掘频繁2-项集 ：" +
                        this.apriori.getFreqKItemSet(2, this.apriori.getFreq1ItemSet().keySet())
        );
    }

    @Test
    public void testGetFreq3ItemSet() {
        System.out.println(
                "挖掘频繁3-项集 ：" +
                        this.apriori.getFreqKItemSet(
                                3,
                                this.apriori.getFreqKItemSet(2, this.apriori.getFreq1ItemSet().keySet()).keySet()
                        )
        );
    }

    @Test
    public void testGetFreqItemSet() {
        this.apriori.mineFreqItemSet(); // 挖掘频繁项集
        System.out.println("挖掘频繁项集 ：" + this.apriori.getFreqItemSet());
    }

    @Test
    public void testMineAssociationRules() {
        this.apriori.mineFreqItemSet(); // 挖掘频繁项集
        this.apriori.mineAssociationRules();
        System.out.println("挖掘频繁关联规则 ：" + this.apriori.getAssiciationRules());
    }
}
