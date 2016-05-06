package com.xugang.apriori;

/**
 * Created by XuGang on 2016/5/6.
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
public class AprioriAlgorithm {
    private Map<Integer, Set<String>> txDatabase; // 事务数据库
    private Float minSup; // 最小支持度
    private Float minConf; // 最小置信度
    private Integer txDatabaseCount; // 事务数据库中的事务数

    private Map<Integer, Set<Set<String>>> freqItemSet; // 频繁项集集合
    private Map<Set<String>, Set<Set<String>>> assiciationRules; // 频繁关联规则集合

    public AprioriAlgorithm(Map<Integer, Set<String>> txDatabase,Float minSup,Float minConf) {
        this.txDatabase = txDatabase;
        this.minSup = minSup;
        this.minConf = minConf;
        this.txDatabaseCount = this.txDatabase.size();
        freqItemSet = new TreeMap<Integer, Set<Set<String>>>();
        assiciationRules = new HashMap<Set<String>, Set<Set<String>>>();
    }

    public Map<Set<String>, Float> getFreq1ItemSet() {
        Map<Set<String>, Float> freq1ItemSetMap = new HashMap<Set<String>, Float>();
        Map<Set<String>, Integer> candFreq1ItemSet = this.getCandFreq1ItemSet();
        Iterator<Map.Entry<Set<String>, Integer>> it = candFreq1ItemSet.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Set<String>, Integer> entry = it.next();
            // 计算支持度
            Float supported = new Float(entry.getValue().toString())/new Float(txDatabaseCount);
            if(supported>=minSup) {
                freq1ItemSetMap.put(entry.getKey(), supported);
            }
        }
        return freq1ItemSetMap;
    }

    public Map<Set<String>, Integer> getCandFreq1ItemSet() {
        Map<Set<String>, Integer> candFreq1ItemSetMap = new HashMap<Set<String>, Integer>();
        Iterator<Map.Entry<Integer, Set<String>>> it = txDatabase.entrySet().iterator();
        // 统计支持数，生成候选频繁1-项集
        while(it.hasNext()) {
            Map.Entry<Integer, Set<String>> entry = it.next();
            Set<String> itemSet = entry.getValue();
            for(String item : itemSet) {
                Set<String> key = new HashSet<String>();
                key.add(item.trim());
                if(!candFreq1ItemSetMap.containsKey(key)) {
                    Integer value = 1;
                    candFreq1ItemSetMap.put(key, value);
                }
                else {
                    Integer value = 1+candFreq1ItemSetMap.get(key);
                    candFreq1ItemSetMap.put(key, value);
                }
            }
        }
        return candFreq1ItemSetMap;
    }

    public Set<Set<String>> aprioriGen(int m, Set<Set<String>> freqMItemSet) {
        Set<Set<String>> candFreqKItemSet = new HashSet<Set<String>>();
        Iterator<Set<String>> it = freqMItemSet.iterator();
        Set<String> originalItemSet = null;
        while(it.hasNext()) {
            originalItemSet = it.next();
            Iterator<Set<String>> itr = this.getIterator(originalItemSet, freqMItemSet);
            while(itr.hasNext()) {
                Set<String> identicalSet = new HashSet<String>(); // 两个项集相同元素的集合(集合的交运算)
                identicalSet.addAll(originalItemSet);
                Set<String> set = itr.next();
                identicalSet.retainAll(set); // identicalSet中剩下的元素是identicalSet与set集合中公有的元素
                if(identicalSet.size() == m-1) { // (k-1)-项集中k-2个相同
                    Set<String> differentSet = new HashSet<String>(); // 两个项集不同元素的集合(集合的差运算)
                    differentSet.addAll(originalItemSet);
                    differentSet.removeAll(set); // 因为有k-2个相同，则differentSet中一定剩下一个元素，即differentSet大小为1
                    differentSet.addAll(set); // 构造候选k-项集的一个元素(set大小为k-1,differentSet大小为k)
                    candFreqKItemSet.add(differentSet); // 加入候选k-项集集合
                }
            }
        }
        return candFreqKItemSet;
    }
    private Iterator<Set<String>> getIterator(Set<String> itemSet, Set<Set<String>> freqKItemSet) {
        Iterator<Set<String>> it = freqKItemSet.iterator();
        while(it.hasNext()) {
            if(itemSet.equals(it.next())) {
                break;
            }
        }
        return it;
    }

    public Map<Set<String>, Float> getFreqKItemSet(int k, Set<Set<String>> freqMItemSet) {
        Map<Set<String>, Integer> candFreqKItemSetMap = new HashMap<Set<String>, Integer>();
// 调用aprioriGen方法，得到候选频繁k-项集
        Set<Set<String>> candFreqKItemSet = this.aprioriGen(k-1, freqMItemSet);

// 扫描事务数据库
        Iterator<Map.Entry<Integer, Set<String>>> it = txDatabase.entrySet().iterator();
// 统计支持数
        while(it.hasNext()) {
            Map.Entry<Integer, Set<String>> entry = it.next();
            Iterator<Set<String>> kit = candFreqKItemSet.iterator();
            while(kit.hasNext()) {
                Set<String> kSet = kit.next();
                Set<String> set = new HashSet<String>();
                set.addAll(kSet);
                set.removeAll(entry.getValue()); // 候选频繁k-项集与事务数据库中元素做差元算
                if(set.isEmpty()) { // 如果拷贝set为空，支持数加1
                    if(candFreqKItemSetMap.get(kSet) == null) {
                        Integer value = 1;
                        candFreqKItemSetMap.put(kSet, value);
                    }
                    else {
                        Integer value = 1+candFreqKItemSetMap.get(kSet);
                        candFreqKItemSetMap.put(kSet, value);
                    }
                }
            }
        }
// 计算支持度，生成频繁k-项集，并返回
        return support(candFreqKItemSetMap);
    }


    public Map<Set<String>, Float> support(Map<Set<String>, Integer> candFreqKItemSetMap) {
        Map<Set<String>, Float> freqKItemSetMap = new HashMap<Set<String>, Float>();
        Iterator<Map.Entry<Set<String>, Integer>> it = candFreqKItemSetMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Set<String>, Integer> entry = it.next();
// 计算支持度
            Float supportRate = new Float(entry.getValue().toString())/new Float(txDatabaseCount);
            if(supportRate<minSup) { // 如果不满足最小支持度，删除
                it.remove();
            }
            else {
                freqKItemSetMap.put(entry.getKey(), supportRate);
            }
        }
        return freqKItemSetMap;
    }


    public void mineFreqItemSet() {
// 计算频繁1-项集
        Set<Set<String>> freqKItemSet = this.getFreq1ItemSet().keySet();
        freqItemSet.put(1, freqKItemSet);
// 计算频繁k-项集(k>1)
        int k = 2;
        while(true) {
            Map<Set<String>, Float> freqKItemSetMap = this.getFreqKItemSet(k, freqKItemSet);
            if(!freqKItemSetMap.isEmpty()) {
                this.freqItemSet.put(k, freqKItemSetMap.keySet());
                freqKItemSet = freqKItemSetMap.keySet();
            }
            else {
                break;
            }
            k++;
        }
    }


    public void mineAssociationRules() {
        freqItemSet.remove(1); // 删除频繁1-项集
        Iterator<Map.Entry<Integer, Set<Set<String>>>> it = freqItemSet.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Integer, Set<Set<String>>> entry = it.next();
            for(Set<String> itemSet : entry.getValue()) {
// 对每个频繁项集进行关联规则的挖掘
                mine(itemSet);
            }
        }
    }


    public void mine(Set<String> itemSet) {
        int n = itemSet.size()/2; // 根据集合的对称性，只需要得到一半的真子集
        for(int i=1; i<=n; i++) {
// 得到频繁项集元素itemSet的作为条件的真子集集合
            Set<Set<String>> properSubset = ProperSubsetCombination.getProperSubset(i, itemSet);
// 对条件的真子集集合中的每个条件项集，获取到对应的结论项集，从而进一步挖掘频繁关联规则
            for(Set<String> conditionSet : properSubset) {
                Set<String> conclusionSet = new HashSet<String>();
                conclusionSet.addAll(itemSet);
                conclusionSet.removeAll(conditionSet); // 删除条件中存在的频繁项
                confide(conditionSet, conclusionSet); // 调用计算置信度的方法，并且挖掘出频繁关联规则
            }
        }
    }


    public void confide(Set<String> conditionSet, Set<String> conclusionSet) {
// 扫描事务数据库
        Iterator<Map.Entry<Integer, Set<String>>> it = txDatabase.entrySet().iterator();
// 统计关联规则支持计数
        int conditionToConclusionCnt = 0; // 关联规则(条件项集推出结论项集)计数
        int conclusionToConditionCnt = 0; // 关联规则(结论项集推出条件项集)计数
        int supCnt = 0; // 关联规则支持计数
        while(it.hasNext()) {
            Map.Entry<Integer, Set<String>> entry = it.next();
            Set<String> txSet = entry.getValue();
            Set<String> set1 = new HashSet<String>();
            Set<String> set2 = new HashSet<String>();
            set1.addAll(conditionSet);

            set1.removeAll(txSet); // 集合差运算：set-txSet
            if(set1.isEmpty()) { // 如果set为空，说明事务数据库中包含条件频繁项conditionSet
// 计数
                conditionToConclusionCnt++;
            }
            set2.addAll(conclusionSet);
            set2.removeAll(txSet); // 集合差运算：set-txSet
            if(set2.isEmpty()) { // 如果set为空，说明事务数据库中包含结论频繁项conclusionSet
// 计数
                conclusionToConditionCnt++;

            }
            if(set1.isEmpty() && set2.isEmpty()) {
                supCnt++;
            }
        }
// 计算置信度
        Float conditionToConclusionConf = new Float(supCnt)/new Float(conditionToConclusionCnt);
        if(conditionToConclusionConf>=minConf) {
            if(assiciationRules.get(conditionSet) == null) { // 如果不存在以该条件频繁项集为条件的关联规则
                Set<Set<String>> conclusionSetSet = new HashSet<Set<String>>();
                conclusionSetSet.add(conclusionSet);
                assiciationRules.put(conditionSet, conclusionSetSet);
            }
            else {
                assiciationRules.get(conditionSet).add(conclusionSet);
            }
        }
        Float conclusionToConditionConf = new Float(supCnt)/new Float(conclusionToConditionCnt);
        if(conclusionToConditionConf>=minConf) {
            if(assiciationRules.get(conclusionSet) == null) { // 如果不存在以该结论频繁项集为条件的关联规则
                Set<Set<String>> conclusionSetSet = new HashSet<Set<String>>();
                conclusionSetSet.add(conditionSet);
                assiciationRules.put(conclusionSet, conclusionSetSet);
            }
            else {
                assiciationRules.get(conclusionSet).add(conditionSet);
            }
        }
    }

    public Map<Integer, Set<Set<String>>> getFreqItemSet() {
        return freqItemSet;
    }

    public Map<Set<String>, Set<Set<String>>> getAssiciationRules() {
        return assiciationRules;
    }
}
