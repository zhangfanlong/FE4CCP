package ExtractFeatures;

import java.io.File;
import java.io.IOException;

import Global.VariationInformation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

public class WekaOperations {
    
	//将特征从内存中写出成ARFF文件
	public static void WriteFeaturesToArff(String path,int flag){
		FastVector atts;  
		FastVector attVals; 
        Instances data;  
        Instances dataRel;  
        double[] vals;  
        double[] valsRel;  
        
        // 1. set up attributes  
        atts = new FastVector();   
        
        //代码特征   10
        atts.addElement(new Attribute("sourceLine")); 
        //四个heathed度量
        atts.addElement(new Attribute("uniOPERATORCount")); 
        atts.addElement(new Attribute("uniOperandCount")); 
        atts.addElement(new Attribute("totalOPERATORCount")); 
        atts.addElement(new Attribute("totalOperandCount"));        
        //函数信息
        atts.addElement(new Attribute("totalMethodInvocCount")); 
        atts.addElement(new Attribute("libraryMethodInvocCount")); 
        atts.addElement(new Attribute("localMethodInvocCount")); 
        atts.addElement(new Attribute("otherMethodInvocCount")); 
        atts.addElement(new Attribute("totalParameterCount")); 
        
        
        //上下文特征    9
        attVals = new FastVector();  
        attVals.addElement(1);
        attVals.addElement(0);
        atts.addElement(new Attribute("isLocalClone"));  
        atts.addElement(new Attribute("simFileName")); 
        atts.addElement(new Attribute("simMaskedFileName")); 
        atts.addElement(new Attribute("simMethodName")); 
        atts.addElement(new Attribute("simTotalParaName")); 
        atts.addElement(new Attribute("simMaxParaName")); 
        atts.addElement(new Attribute("simTotalParaType")); 
        atts.addElement(new Attribute("isSameBlockInfo"));
        atts.addElement(new Attribute("simCloneFragments")); 
        
       
        //结构特征   9
        atts.addElement(new Attribute("this_or_super")); 
        atts.addElement(new Attribute("assignment")); 
        atts.addElement(new Attribute("identifier")); 
        //atts.addElement(new Attribute("literal"));        
        atts.addElement(new Attribute("if_then_statement")); 
        atts.addElement(new Attribute("if_then_else_statement")); 
        atts.addElement(new Attribute("switch_statement")); 
        atts.addElement(new Attribute("while_statement")); 
        atts.addElement(new Attribute("do_statement"));
        atts.addElement(new Attribute("for_statement")); 
      
       /* if(flag == 1){
        	atts.addElement(new Attribute("CONSISTENTCHANGE"));
        }
        */
        if(flag == 2){ //第二种特征
        	atts.addElement(new Attribute("STATIC"));
        	atts.addElement(new Attribute("SAME"));
        	atts.addElement(new Attribute("ADD"));
        	atts.addElement(new Attribute("DELETE"));
        	atts.addElement(new Attribute("SPLIT"));
        	
        	atts.addElement(new Attribute("CONSISTENTCHANGE"));
        	atts.addElement(new Attribute("INCONSISTENTCHANGE"));
        	
        	atts.addElement(new Attribute("cloneAge"));
        }
        
        //一致性维护label
        atts.addElement(new Attribute("consistency")); 
        

        // 2. create Instances object  
        data = new Instances("FeatureVectors", atts, 0);  //????   0啥意思   ???????????????????????
        // 3. fill with data  
		for(FeatureVector vec : VariationInformation.featureVectorList){
			vals = new double[data.numAttributes()];  
			vals[0] = vec.getSourceLine();
			vals[1] = vec.getUniOPERATORCount();
			vals[2] = vec.getUniOperandCount();
			vals[3] = vec.getTotalOPERATORCount();
			vals[4] = vec.getTotalOperandCount();
			vals[5] = vec.getTotalMethodInvocCount();
			vals[6] = vec.getLibraryMethodInvocCount();
			vals[7] = vec.getLocalMethodInvocCount();
			vals[8] = vec.getOtherMethodInvocCount();
			vals[9] = vec.getTotalParameterCount();
			if(vec.isLocalClone()) vals[10] = 1;
			else vals[10] = 0;
			vals[11] = vec.getSimFileName();
			vals[12] = vec.getSimMaskedFileName();
			vals[13] = vec.getSimMethodName();
			vals[14] = vec.getSimTotalParaName();
			vals[15] = vec.getSimMaxParaName();
			vals[16] = vec.getSimTotalParaType();
			if(vec.getIsSameBlockInfo()) vals[17] = 1;
			else vals[17] = 0;
			vals[18] = vec.getSimCloneFragment();
			vals[19] = vec.getStruFeature()[0];
			vals[20] = vec.getStruFeature()[1];
			vals[21] = vec.getStruFeature()[2];
			vals[22] = vec.getStruFeature()[3];
			vals[23] = vec.getStruFeature()[4];
			vals[24] = vec.getStruFeature()[5];
			vals[25] = vec.getStruFeature()[6];
			vals[26] = vec.getStruFeature()[7];
			vals[27] = vec.getStruFeature()[8];	
			/*if(flag == 1){
				vals[28] = vec.getConsisCount();
	        }*/
			if(flag==2){
				vals[28] = vec.getEvoPattern()[0];
				vals[29] = vec.getEvoPattern()[1];
				vals[30] = vec.getEvoPattern()[2];
				vals[31] = vec.getEvoPattern()[3];
				vals[32] = vec.getEvoPattern()[4];	
				vals[33] = vec.getEvoPattern()[5];
				vals[34] = vec.getEvoPattern()[6];	
				vals[35] = vec.getAge();	
			}
			
			vals[vals.length-1] = vec.getConsistence();
			
	        data.add(new Instance(1.0, vals));// add   ？？？？？？？？？？？？？？？？？？？？？？？？
		}
		
		// 4. output data  
        System.out.println(data); 
        
        // 5. save arff  
        ArffSaver saver = new ArffSaver();  
        saver.setInstances(data);  
        try {
			saver.setFile(new File(path));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	
	/*
     * 从.arff文件中获取样本Instances;
     * 1.fileName instances的文件名
     */
    public static Instances getInstances(String fileName) throws Exception{
       File file= new File(fileName);
       return getInstances(file);
    }
 
    
    /*
     * 从.arff文件中获取样本Instances;
     * 1.file 获得instances的File对象
     */
    public static Instances getInstances(File file) throws Exception{
       Instances inst = null;
       try{
           ArffLoader loader = new ArffLoader();
           loader.setFile(file);
           inst = loader.getDataSet();
       }
       catch(Exception e){
           throw new Exception(e.getMessage());
       }
       return inst;
    }
 
    
}
