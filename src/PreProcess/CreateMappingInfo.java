package PreProcess;

import java.util.ArrayList;
import java.util.List;





import PreProcess.CreateCRDInfo.CRDMatchLevel;
import CRDInformation.CloneSourceInfo;
import CloneRepresentation.CRD;
import CloneRepresentation.CloneFragment;
import CloneRepresentation.CloneGroup;
import CloneRepresentation.EvolutionPattern;
import CloneRepresentation.FragmentMapping;
import CloneRepresentation.GroupMapping;
import Global.VariationInformation;

abstract class Mapping{
    public String ID; //ӳ���ϵ���
}

class  CGInfo {  //CG��Ϣ�ṹ�壬����CG��ID�ʹ�С��������¡Ƭ�ε�������
    public String id;
    public int size;
}

class MappingList extends ArrayList { }

class CloneFragmentMapping extends Mapping{
    public String SrcCFID;
    public String DestCFID;

    //����ӳ�������CF��CRD����Ϊ����ʶ�����ģʽʱ������textSim����Ϊ��MapCF�в����㣩
/*  public CloneRegionDescriptor srcCrd;
    public CloneRegionDescriptor destCrd;*/

    public CRDMatchLevel CrdMatchLevel; //�������ԣ�������������CF��CRDƥ�伶��

    public float textSim;  //����Դ������ı����ƶȣ���diff��Ϣ�л��
    //�������ο�¡�����λ����Ϣ�����ڼ���diffʱ��ȡ��sourceInfos[0]ΪԴ��Ϣ��sourceInfos[1]ΪĿ����Ϣ
    public CloneSourceInfo[] sourceInfos = new CloneSourceInfo[2];
}


class CloneGroupMapping extends Mapping{

    public CGInfo srcCGInfo;    //Դ��¡Ⱥ��Ϣ
    public CGInfo destCGInfo;   //Ŀ���¡Ⱥ��Ϣ
    public EvolutionPattern EvoPattern;    //�������ģʽ��Ϣ
    public MappingList CFMapList;  //����CFӳ����б�
    
    // Ϊ��ȷ��ƥ�������CG����һ��ӳ���ϵ����
    public int CreateCGMapping(CloneGroup srcGroupEle, CloneGroup destGroupEle,int mapCount){
    	this.srcCGInfo = new CGInfo();
    	this.srcCGInfo.id = String.valueOf(srcGroupEle.getCGID());
    	this.srcCGInfo.size = srcGroupEle.getNumberofCF();
    	
    	this.destCGInfo = new CGInfo();
    	this.destCGInfo.id = String.valueOf(destGroupEle.getCGID());
    	this.destCGInfo.size = destGroupEle.getNumberofCF();

        //ӳ���¡Ⱥ�ڵĿ�¡Ƭ��ӳ�䣬������浽CFMapList��Ա��
        this.CFMapList = MapCF(srcGroupEle, destGroupEle);
        this.ID = String.valueOf(++mapCount);
        return mapCount;
    }
    
    //����textSim��MapCF����
    public MappingList MapCF(CloneGroup srcCG, CloneGroup destCG){
    	
        MappingList cfMappingList = new MappingList();
        //�ֱ��ȡ������¡Ⱥ�п�¡Ƭ�ε�CRDԪ���б�
        List<CRD> srcCGCrdList = new ArrayList<CRD>();
        List<CRD> destCGCrdList = new ArrayList<CRD>();
        for(CloneFragment frag : srcCG.getClonefragment()){
        	srcCGCrdList.add(frag.getCRD());
        }
        for(CloneFragment frag : destCG.getClonefragment()){
        	destCGCrdList.add(frag.getCRD());
        }
 
        int i, j;
        //��������������鱣��srcCF��destCF��ӳ�����
        boolean[] srcCFMapped = new boolean[srcCGCrdList.size()];
        for (i = 0; i < srcCGCrdList.size(); i++)
        	srcCFMapped[i] = false; 
        boolean[] destCFMapped = new boolean[destCGCrdList.size()];
        for (j = 0; j < destCGCrdList.size(); j++)
        	destCFMapped[j] = false; 

        //��ʼӳ��
        if (srcCGCrdList != null && destCGCrdList != null && srcCGCrdList.size() != 0 && destCGCrdList.size() != 0){
            //�������󱣴�CRDMatch���
            CRDMatchLevel[][] crdMatchMatrix = new CRDMatchLevel[srcCGCrdList.size()][destCGCrdList.size()];
            //�������󱣴�textSim���
            float[][] textSimMatrix = new float[srcCGCrdList.size()][destCGCrdList.size()];

            i = -1;
            int mapCount = 0;

            //��һ����Ϊÿ��srcCF��destCF����textSim��CRDMatchLevel
            for (CRD srcCRD : srcCGCrdList){
                i++;
                j = -1;
                for (CRD destCRD : destCGCrdList){
                    j++;
                    if (!destCFMapped[j]){
                        CRDMatchLevel matchLevel = CreateCRDInfo.GetCRDMatchLevel(srcCRD, destCRD);
                        crdMatchMatrix[i][j] = matchLevel;
                        textSimMatrix[i][j] = CreateCRDInfo.GetTextSimilarity(srcCRD, destCRD, true); //���һ������ָ���Ƿ���Կ���
                    }
                }
            }
 
            // �ڶ���������textSim��CRDMatchLevel����ͬȷ��CFӳ��
            for (i = 0; i < srcCGCrdList.size(); i++){
                //������ӳ���srcCF
                if (!srcCFMapped[i]){ 
                    int maxTextSimIndex = -1;   //���ڼ�¼����textSim��Ӧ��destCF����
                    int maxMatchLevelIndex = -1;    //���ڼ�¼��ߵ�CRDMatchLevel��Ӧ��destCF������
                    float maxTextSim = CreateCRDInfo.defaultTextSimTh;  //���ڼ�¼����textSimֵ������Ϊ��ֵ
                    CRDMatchLevel maxMatchLevel = CRDMatchLevel.DIFFERENT;
                    for (j = 0; j < destCGCrdList.size(); j++){                 
                        if (!destCFMapped[j]){             
                            if (textSimMatrix[i][j] >= maxTextSim)   //��ȡ����textSim������
                            { maxTextSim = textSimMatrix[i][j]; maxTextSimIndex = j; }
                            if (crdMatchMatrix[i][j].ordinal() > maxMatchLevel.ordinal())   //��ȡ��ߵ�CRDMatchLevel������
                            { maxMatchLevel = crdMatchMatrix[i][j]; maxMatchLevelIndex = j; }
                        }
                        else continue; 
                    }
                    if (maxTextSimIndex > -1 || maxMatchLevelIndex > -1){  //����ҵ�
                        int finalIndex;
                        if(maxTextSimIndex == -1){
                        	finalIndex = maxMatchLevelIndex;
                        }else if(maxMatchLevelIndex == -1){
                        	finalIndex = maxTextSimIndex;
                        }else if (maxTextSimIndex == maxMatchLevelIndex){  //���������ͬһ��destCF�ϣ��򴴽�ӳ��       
                            finalIndex = maxTextSimIndex;
                        }else if (crdMatchMatrix[i][maxTextSimIndex].ordinal() < maxMatchLevel.ordinal()){    
                            finalIndex = maxMatchLevelIndex;
                        }else if (textSimMatrix[i][maxMatchLevelIndex] < maxTextSim){  
                            finalIndex = maxTextSimIndex;
                        }else{//������textSim��CRDMatchLevel���޷�ȷ��ʱ��ѡindex��ӽ�i��һ��
                            if (Math.abs(maxTextSimIndex - i) < Math.abs(maxMatchLevelIndex - i))
                            { finalIndex = maxTextSimIndex; }
                            else
                            { finalIndex = maxMatchLevelIndex; }
                        }
                        CloneFragmentMapping cfMapping = new CloneFragmentMapping();
                        cfMapping.ID = String.valueOf(++mapCount);
                        cfMapping.SrcCFID = String.valueOf(i + 1);
                        cfMapping.DestCFID = String.valueOf(finalIndex + 1);
                        cfMapping.textSim = textSimMatrix[i][finalIndex];
                        cfMapping.CrdMatchLevel = crdMatchMatrix[i][finalIndex];
                        cfMappingList.add(cfMapping);
                        srcCFMapped[i] = true;
                        destCFMapped[finalIndex] = true;
                    }
                }
            }
        }
        return cfMappingList;
    }
}

public class CreateMappingInfo extends Mapping{
	private String srcFileName;  //����ӳ���Դ�ļ���-withCRD.xml�ļ�����
	private String destFileName;

	private List<CloneGroupMapping> CGMapList;
	private List UnMappedSrcCGList;
	private List UnMappedDestCGList;

	public void MapBetweenVersions(List<CloneGroup> srcGroupList, List<CloneGroup> destGroupList){//����Ϊһ���汾��CloneGroup

		//�ó�ʼ״̬
		CGMapList = new ArrayList<CloneGroupMapping>();
		UnMappedSrcCGList = null;
        UnMappedDestCGList = null;
        int mapCount = 0; //����ͳ��CGӳ�������
      
        int[][] cgMatchLevelMatrix = new int[srcGroupList.size()][destGroupList.size()];
        float[][] cgLocationOverlapMatrix = new float[srcGroupList.size()][destGroupList.size()];
        for(int i=0;i<srcGroupList.size();i++){
        	for(int j=0;j<destGroupList.size();j++){
        		cgMatchLevelMatrix[i][j] = -1; //-1��ʾδ����matchLevel
        		cgLocationOverlapMatrix[i][j] = -1;
        	}
        }
        
        boolean[] isSrcCGMapped = new boolean[srcGroupList.size()];   //Դ��¡Ⱥӳ��������
        boolean[] isDestCGMapped = new boolean[destGroupList.size()];   //Ŀ���¡Ⱥӳ��������
        for (int srcIndex = 0; srcIndex < srcGroupList.size(); srcIndex++)
        	isSrcCGMapped[srcIndex] = false; 
        for (int destIndex = 0; destIndex < destGroupList.size(); destIndex++)
        	isDestCGMapped[destIndex] = false; 
        
        //��һ��ӳ�䣨��METHODINFOMATCH������ӳ�䣩
        for(int i=0;i<srcGroupList.size();i++){
        	for(int j=0;j<destGroupList.size();j++){
        		if(!isDestCGMapped[j] && IsCGMatch(srcGroupList.get(i),destGroupList.get(j),CRDMatchLevel.METHODINFOMATCH)){
        			//�������CGƥ�䣬����һ��CGӳ���ϵ���󣬼���ӳ���б���
                    CloneGroupMapping cgMapping = new CloneGroupMapping();
                    mapCount = cgMapping.CreateCGMapping(srcGroupList.get(i), destGroupList.get(j),mapCount);
                    //����CGӳ���ϵ����CGMapList��
                    this.CGMapList.add(cgMapping);
                    isSrcCGMapped[i] = true;
                    isDestCGMapped[j] = true; //����ӳ����Ϊ��
                    break;  //Դ��¡Ⱥ�ҵ���Ӧ�ģ�һ����CG�󣬲��ټ���Ѱ��
                }
                else continue;
        	}
        }
		
        //�ڶ���ӳ�䣨��METHODNAMEMATCH������ӳ�䣬���һ�ֻ������ƣ�
        for(int i=0;i<srcGroupList.size();i++){
        	if(!isSrcCGMapped[i]){
        		for(int j=0;j<destGroupList.size();j++){
        			 if (!isDestCGMapped[j] && IsCGMatch(srcGroupList.get(i),destGroupList.get(j), CRDMatchLevel.METHODNAMEMATCH)){
        				 CloneGroupMapping cgMapping = new CloneGroupMapping();
        				 mapCount = cgMapping.CreateCGMapping(srcGroupList.get(i), destGroupList.get(j),mapCount);
                         this.CGMapList.add(cgMapping);
                         isSrcCGMapped[i] = true;
                         isDestCGMapped[j] = true;
                         break;
                     }
        			 else continue;
        		}
        	}
        	else continue;
        }
        
        //������ӳ�䣨��FILECLASSMATCH������ӳ�䣬���һ�ֻ������ƣ�
        for(int i=0;i<srcGroupList.size();i++){
        	if(!isSrcCGMapped[i]){
        		for(int j=0;j<destGroupList.size();j++){
        			 if (!isDestCGMapped[j] && IsCGMatch(srcGroupList.get(i),destGroupList.get(j), CRDMatchLevel.FILECLASSMATCH)){
        				 CloneGroupMapping cgMapping = new CloneGroupMapping();
        				 mapCount = cgMapping.CreateCGMapping(srcGroupList.get(i), destGroupList.get(j),mapCount);
                         this.CGMapList.add(cgMapping);
                         isSrcCGMapped[i] = true;
                         isDestCGMapped[j] = true;
                         break;
                     }
        			 else continue;
        		}
        	}
        	else continue;
        }
        
        //��ǰ���ֺ�δ��ӳ���Դ��¡Ⱥ����UnMappedSrcCGList
        for(int i=0;i<srcGroupList.size();i++){
        	if(!isSrcCGMapped[i]){
        		if (this.UnMappedSrcCGList == null)
        			this.UnMappedSrcCGList = new MappingList(); 
                CGInfo info = new CGInfo();
                info.id = String.valueOf(srcGroupList.get(i).getCGID());
                info.size = srcGroupList.get(i).getNumberofCF();
                
                this.UnMappedSrcCGList.add(info);
        	}
        }
        
        //������ӳ�䣨����һ�Զ���������METHODNAMEMATCH�����Ͽ��飬��ǰ�������ƣ�ֻ�Ǵ�dest��src����
        for(int j=0;j<destGroupList.size();j++){
        	if(!isDestCGMapped[j]){ //ֻ����δ��ӳ���destCG
        		for(int i=0;i<srcGroupList.size();i++){ 
        			//ֻ������ӳ���Դ��¡Ⱥ���������㣬�򲻽��к���Ƚϣ�
        			 if (isSrcCGMapped[i]  && IsCGMatch(srcGroupList.get(i),destGroupList.get(j), CRDMatchLevel.METHODNAMEMATCH)){
        				 //���ִ��ӳ���б��в���srcClassEle���ڵ�ӳ�䣬����һ��ӳ������������λ�ã�ʹ����ͳһԴ������ӳ��λ��������
        				 int mapIndex = -1;
                         for (CloneGroupMapping cgMap : this.CGMapList){
                             mapIndex++;
                             String classId = String.valueOf(srcGroupList.get(i).getCGID());
                             if (classId != null && cgMap.srcCGInfo.id.equals(classId)){
                                 CloneGroupMapping cgMapping = new CloneGroupMapping();
                                 mapCount = cgMapping.CreateCGMapping(srcGroupList.get(i), destGroupList.get(j),mapCount);
                                 //����CGӳ���ϵ����CGMapList��mapIndex+1��λ��
                                 this.CGMapList.add(mapIndex + 1, cgMapping);
                                 isDestCGMapped[j] = true; //����ӳ����Ϊ��                                       
                                 break;
                             }
                         }
                         break;
                     }
        			 else continue;
        		}
        	}
        }
        
        //��δ�ҵ�Դ��¡Ⱥ��Ŀ���¡Ⱥ����UnMappedDestCGList
        for(int j=0;j<destGroupList.size();j++){
        	if (!isDestCGMapped[j]){
                if (this.UnMappedDestCGList == null)
                	this.UnMappedDestCGList = new MappingList();
                CGInfo info = new CGInfo();
                info.id = String.valueOf(destGroupList.get(j).getCGID());
                info.size = destGroupList.get(j).getNumberofCF();

            	this.UnMappedDestCGList.add(info);
            }
        }
	}
	

	private boolean IsCGMatch(CloneGroup srcCG, CloneGroup destCG, CRDMatchLevel matchLevelThres){ // ����CRDƥ�伶��λ�ø����ʣ������ı����ƶȣ��ж�����CG�Ƿ񹹳�ӳ��
		
        float overlapTh = -1;  //ָ����������ֵ   
        for(CloneFragment srcCF :  srcCG.getClonefragment()){
        	CRD srcCRD = srcCF.getCRD();
        	for(CloneFragment destCF : destCG.getClonefragment()){
        		CRD destCRD = destCF.getCRD();
        		CRDMatchLevel matchLevel = CreateCRDInfo.GetCRDMatchLevel(srcCRD, destCRD);
        		if ( matchLevel.ordinal() >= matchLevelThres.ordinal()){
                    if (matchLevelThres.ordinal() >= CRDMatchLevel.METHODNAMEMATCH.ordinal()){
                   	 
                        if (matchLevelThres.ordinal() == CRDMatchLevel.METHODINFOMATCH.ordinal()){ 
                        	overlapTh = CreateCRDInfo.locationOverlap1; 
                        }
                        else if (matchLevelThres.ordinal() == CRDMatchLevel.METHODNAMEMATCH.ordinal()){ 
                        	overlapTh = CreateCRDInfo.locationOverlap2; 
                        }

                        float overlap = CreateCRDInfo.GetLocationOverlap(srcCRD, destCRD);  //����overlap
                        if (overlap >= overlapTh)return true; 
                        else{//��λ�ø����ʲ����ж�ʱ��ʹ���ı����ƶ�             	 
                            float textSim = CreateCRDInfo.GetTextSimilarity(srcCRD, destCRD, true);
                            if (textSim >= CreateCRDInfo.defaultTextSimTh)	return true; 
                        }
                    }
                    if (matchLevelThres.ordinal() == CRDMatchLevel.FILECLASSMATCH.ordinal()){    //��FILECLASSMATCH������ֱ��ʹ���ı����ƶ�
                    
                        float textSim = CreateCRDInfo.GetTextSimilarity(srcCRD, destCRD, true);
                        if (textSim >= CreateCRDInfo.defaultTextSimTh)	return true;
                    }
                }
        	}
        }
        
        return false;
	}
	
	//ʶ���¡Ⱥ�Ľ���ģʽ 
	// �µĽ���ģʽʶ�𷽷�
	
	//ģʽ����Ϊ1,2,3
	//1��¡����ȫ����¡����Ƭ�η�����ͬ�ı仯Ϊһ���Ա仯����С�Ķ���,����1Ŀǰ������
	//2��¡����������������¡����Ƭ�η�����ͬ�ı仯Ϊһ���Ա仯���м�Ķ���
	//3��¡����ֻҪͬʱ�����仯����һ���Ա仯�����Ķ���
	public void RecognizeEvolutionPattern(){
    	boolean[] atGroupFlag;
        if (this.CGMapList.size() > 0){
        	atGroupFlag = new boolean[this.CGMapList.size()];		
        	for (CloneGroupMapping cgMap : this.CGMapList) {

    			//ģʽ����ʶ����ݿ�¡���С��ӳ���������жԱ�
    			//�����Ѿ�ӳ���ϵĽ����ж�һ���Ա仯���߲�һ�±仯
    	
				if (cgMap.EvoPattern == null)
					cgMap.EvoPattern = new EvolutionPattern();

				//�������䣬����ȫ��ӳ����

				if ((cgMap.srcCGInfo.size == cgMap.destCGInfo.size) && (cgMap.srcCGInfo.size == cgMap.CFMapList.size())){	
					
					cgMap.EvoPattern.setSAME(true);// ʶ��SAMEģʽ
					
					/*
					 //����1 ��¡����ȫ����¡����Ƭ�η�����ͬ�ı仯Ϊһ���Ա仯����С�Ķ���			
					

					
					//��¡����ȫ����¡����Ƭ�η�����ͬ�ı仯Ϊһ���Ա仯
					//�����⣬�����仯
					boolean equal = true;
					float textSim = ((CloneFragmentMapping)cgMap.CFMapList.get(0)).textSim;
					for (int k = 1; k < cgMap.CFMapList.size(); k++) {
						if (Math.abs(((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim - textSim) > 0.007) {
							equal = false;
							break;
						}
					}
					if(equal){
						if(textSim < 1) cgMap.EvoPattern.setCONSISTENTCHANGE(true);
						else cgMap.EvoPattern.setSTATIC(true);
					}else { 
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}
					*/
					
					//����2 ��¡����������������¡����Ƭ�η�����ͬ�ı仯Ϊһ���Ա仯���м�Ķ���						
					boolean Static = true;
					for(int m=0;m<cgMap.CFMapList.size()-1; m++){
						float textSim_m = ((CloneFragmentMapping) cgMap.CFMapList.get(m)).textSim;
						for(int n=m+1;n<cgMap.CFMapList.size(); n++){
							float textSim_n = ((CloneFragmentMapping) cgMap.CFMapList.get(n)).textSim;
							if (textSim_m < 1 && textSim_n < 1) {
								//����ͬ�仯����ֵ,ԭ��Ĭ��Ϊ0.0000001�����ڵ���Ϊ0.007
								if(Math.abs(textSim_m - textSim_n) <= 0.007 ) {
									cgMap.EvoPattern.setCONSISTENTCHANGE(true);
								}
								Static = false;
							} else if(Math.abs(textSim_m - 1) <= 0.0000001 && Math.abs(textSim_n - 1) <= 0.0000001){
								if (Static)	Static = true; 
							} else {
								if (Static)	Static = false; 
								}
						}
					}
					if(Static) {
						cgMap.EvoPattern.setSTATIC(true);
					} else {
						if(!cgMap.EvoPattern.isCONSISTENTCHANGE()) {
							cgMap.EvoPattern.setINCONSISTENTCHANGE(true);
						} 
					}
					
					/*
					//3��¡����ֻҪͬʱ�����仯����һ���Ա仯�����Ķ���
					int chanFraCount = 0;
					int unChagFraCount = 0;
					for (int k = 0; k < cgMap.CFMapList.size(); k++) {
						if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim < 1) {
							chanFraCount ++;
						}else if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim == 1) {
							unChagFraCount ++;
						}
					}
					if(unChagFraCount == cgMap.CFMapList.size()) {
						cgMap.EvoPattern.setSTATIC(true);
					} else if(chanFraCount >= 2){
						cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					} else {
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}
						 */
				}
				
				//ӳ���ϵ�С��Ŀ�Ŀ�¡��
				if ((cgMap.CFMapList.size() < cgMap.destCGInfo.size)){
									
					cgMap.EvoPattern.setADD(true);// ʶ��ADDģʽ
					
					/*
					 //����1 ��¡����ȫ����¡����Ƭ�η�����ͬ�ı仯Ϊһ���Ա仯����С�Ķ���	
					boolean equal = true;
					float textSim = ((CloneFragmentMapping)cgMap.CFMapList.get(0)).textSim;
					for (int k = 1; k < cgMap.CFMapList.size(); k++) {
						if (Math.abs(((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim - textSim) > 0.0000001) {
							equal = false;
							break;
						}
					}
					if(equal){
						if(textSim < 1) cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					}else { 
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}
					*/
					
					//����2 ��¡����������������¡����Ƭ�η�����ͬ�ı仯Ϊһ���Ա仯���м�Ķ���	
					boolean Static = true;
					for(int m=0;m<cgMap.CFMapList.size()-1; m++){
						float textSim_m = ((CloneFragmentMapping) cgMap.CFMapList.get(m)).textSim;
						for(int n=m+1;n<cgMap.CFMapList.size(); n++){
							float textSim_n = ((CloneFragmentMapping) cgMap.CFMapList.get(n)).textSim;
							if (textSim_m < 1 && textSim_n < 1) {
								//����ͬ�仯����ֵ,ԭ��Ĭ��Ϊ0.0000001�����ڵ���Ϊ0.007
								if(Math.abs(textSim_m - textSim_n) <= 0.007 ) {
									cgMap.EvoPattern.setCONSISTENTCHANGE(true);
								}
								Static = false;
							} else if(Math.abs(textSim_m - 1) <= 0.007 && Math.abs(textSim_n - 1) <= 0.007){
								if (Static)	Static = true; 
							} else {if (Static)	Static = false; }
						}
					}
					if(Static) {
						cgMap.EvoPattern.setSTATIC(true);
					} else {
						if(!cgMap.EvoPattern.isCONSISTENTCHANGE()) {
							cgMap.EvoPattern.setINCONSISTENTCHANGE(true);
						} 
					}
					
					/*
					//3��¡����ֻҪͬʱ�����仯����һ���Ա仯�����Ķ���
					int chanFraCount = 0;
					for (int k = 0; k < cgMap.CFMapList.size(); k++) {
						if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim < 1) {
							chanFraCount ++;
						}
					}
					if(chanFraCount >= 2){
						cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					} else {
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}*/
				}
	            
				//ӳ����С��Դ��¡��
				if ((cgMap.CFMapList.size() < cgMap.srcCGInfo.size)) {
					
					cgMap.EvoPattern.setSUBSTRACT(true);// ʶ��SUBSTRACTģʽ
					
					/*
					 //����1 ��¡����ȫ����¡����Ƭ�η�����ͬ�ı仯Ϊһ���Ա仯����С�Ķ���	
					boolean equal = true;
					float textSim = ((CloneFragmentMapping)cgMap.CFMapList.get(0)).textSim;
					for (int k = 1; k < cgMap.CFMapList.size(); k++) {
						if (Math.abs(((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim - textSim) > 0.0000001) {
							equal = false;
							break;
						}
					}
					if(equal){
						if(textSim < 1) cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					}else { 
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}
					*/
					
					//����2 ��¡����������������¡����Ƭ�η�����ͬ�ı仯Ϊһ���Ա仯���м�Ķ���	
					boolean Static = true;
					for(int m=0;m<cgMap.CFMapList.size()-1; m++){
						float textSim_m = ((CloneFragmentMapping) cgMap.CFMapList.get(m)).textSim;
						for(int n=m+1;n<cgMap.CFMapList.size(); n++){
							float textSim_n = ((CloneFragmentMapping) cgMap.CFMapList.get(n)).textSim;
							if (textSim_m < 1 && textSim_n < 1) {
								//����ͬ�仯����ֵ,ԭ��Ĭ��Ϊ0.0000001�����ڵ���Ϊ0.007
								if(Math.abs(textSim_m - textSim_n) <= 0.007 ) {
									cgMap.EvoPattern.setCONSISTENTCHANGE(true);
								}
								Static = false;
							} else if(Math.abs(textSim_m - 1) <= 0.007 && Math.abs(textSim_n - 1) <= 0.007){
								if (Static)	Static = true; 
							} else {if (Static)	Static = false; }
						}
					}
					if(Static) {
						cgMap.EvoPattern.setSTATIC(true);
					} else {
						if(!cgMap.EvoPattern.isCONSISTENTCHANGE()) {cgMap.EvoPattern.setINCONSISTENTCHANGE(true);} 
					}
					
					/*
					//3��¡����ֻҪͬʱ�����仯����һ���Ա仯�����Ķ���
					int chanFraCount = 0;
					for (int k = 0; k < cgMap.CFMapList.size(); k++) {
						if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim < 1) {
							chanFraCount ++;
						}
					}
					if(chanFraCount >= 2){
						cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					} else {
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}*/
				}

				
			/*	
				// ʶ��STATICģʽ  --- �°汾�ɰ汾�ж����ڵĿ�¡û�иı�
				boolean textSameFlag = true; // ��Ǹ���CFMap���ı��Ƿ���ȫ��ͬ
				for (int k = 0; k < cgMap.CFMapList.size(); k++) {
					if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim < 1) {
						textSameFlag = false;
						break;
					}
				}
				if (textSameFlag)
					cgMap.EvoPattern.setSTATIC(true);
			*/

				// ʶ��SPLITģʽ
				// �ڽ��п�¡Ⱥӳ��ʱ���ѽ�������ͬԴ��ӳ��������ڵ�λ�ã����ֻ��Ҫ�����ڵ�ǰӳ��ļ���ӳ�伴��
				int i = this.CGMapList.indexOf(cgMap);
				if (!atGroupFlag[i] && i < this.CGMapList.size() - 1) {
					// ������CGMap�뵱ǰcgMapͬԴ��ʱ�򣬼������
					while (i < this.CGMapList.size() - 1
							&& ((CloneGroupMapping) this.CGMapList.get(i + 1)).srcCGInfo.id == cgMap.srcCGInfo.id) {
						if (cgMap.EvoPattern.getMapGroupIDs() == null) { // Ĭ��ֵ��δ��ֵ��Ϊnull
							cgMap.EvoPattern.setMapGroupIDs(cgMap.ID);
							cgMap.EvoPattern.setSPLIT(true);
							cgMap.EvoPattern.setINCONSISTENTCHANGE(true); // ������SPLITģʽ����ط���INCONSISTENTCHANGEģʽ
							atGroupFlag[i] = true;
						}
						cgMap.EvoPattern.setMapGroupIDs(cgMap.EvoPattern.getMapGroupIDs() + ",");
						cgMap.EvoPattern.setMapGroupIDs(cgMap.EvoPattern.getMapGroupIDs() + ((CloneGroupMapping) this.CGMapList.get(i + 1)).ID);
						i++;
					}
					// ���뵱ǰcgMapͬԴ�ļ���CGMap��ΪSPLIT״̬������MapGroupIDs��ֵ
					for (int j = this.CGMapList.indexOf(cgMap) + 1; j <= i; j++) {
						((CloneGroupMapping) this.CGMapList.get(j)).EvoPattern.setSPLIT(true);
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true);// ������SPLITģʽ����ط���INCONSISTENTCHANGEģʽ
						((CloneGroupMapping) this.CGMapList.get(j)).EvoPattern.setMapGroupIDs(cgMap.EvoPattern.getMapGroupIDs());
						atGroupFlag[j] = true;
					}
				}
			}//for
			
        }
       
    }
	
	/*public void SaveMappingForGroup(int srcVersion,int destVersion){
		GroupMapping gSrcMap;
		GroupMapping gDestMap;

		for (CloneGroupMapping cgMap : this.CGMapList){
			gSrcMap = new GroupMapping(); //�����SrcVersion,��Dest
			gDestMap = new GroupMapping();//�����DestVersion,��Src
			//��Ӧ��¡��ӳ����Ϣ
			gSrcMap.setDestVersionID(destVersion);
			gSrcMap.setDestCGID(Integer.parseInt(cgMap.destCGInfo.id));
			gSrcMap.setDestCGSize(cgMap.destCGInfo.size);
			
			gDestMap.setSrcVersionID(srcVersion);
			gDestMap.setSrcCGID(Integer.parseInt(cgMap.srcCGInfo.id));
			gDestMap.setSrcCGSize(cgMap.srcCGInfo.size);
			
			//��Ӧ��¡Ƭ��ӳ����Ϣ
			List<FragmentMapping> fMapList = new ArrayList<FragmentMapping>();
			FragmentMapping fMap = new FragmentMapping();
			for(int k=0;k<cgMap.CFMapList.size();k++){
				fMap.setSrcCFid(Integer.parseInt(((CloneFragmentMapping)cgMap.CFMapList.get(k)).SrcCFID));
				fMap.setDestCFid(Integer.parseInt(((CloneFragmentMapping)cgMap.CFMapList.get(k)).DestCFID));
				fMap.setCRDMatchLevel((((CloneFragmentMapping)cgMap.CFMapList.get(k)).CrdMatchLevel).toString());
				fMap.setTextSim(((CloneFragmentMapping)cgMap.CFMapList.get(k)).textSim);
				
				fMapList.add(fMap);
			}
			
			//��¡��ģʽ��Ϣ
			EvolutionPattern evoPattern = new EvolutionPattern();
			evoPattern.setSTATIC(cgMap.EvoPattern.isSTATIC());
			evoPattern.setSAME(cgMap.EvoPattern.isSAME());
			evoPattern.setADD(cgMap.EvoPattern.isADD());
			evoPattern.setSUBSTRACT(cgMap.EvoPattern.isSUBSTRACT());
			evoPattern.setCONSISTENTCHANGE(cgMap.EvoPattern.isCONSISTENTCHANGE());
			evoPattern.setINCONSISTENTCHANGE(cgMap.EvoPattern.isINCONSISTENTCHANGE());
			evoPattern.setSPLIT(cgMap.EvoPattern.isSPLIT());
			if(cgMap.EvoPattern.getMapGroupIDs() != null){
				evoPattern.setMapGroupIDs(String.valueOf(cgMap.EvoPattern.getMapGroupIDs()));
			}

			//���뵽��Ӧ��Group��
			for(CloneGroup group : VariationInformation.cloneGroup){
				if(group.getVersionID() == srcVersion && group.getCGID() == Integer.parseInt(cgMap.srcCGInfo.id)){
					group.setDestGroupMapping(gSrcMap);
					for(CloneFragment fragment : group.getClonefragment()){
						for(FragmentMapping fraMap : fMapList){
							if(fragment.getCFID() == fraMap.getSrcCFid()){
								fragment.setDestFragmentMapping(fMap);//fMap.getSrcCFid ������ fragment.getCFID()
							}
						}
					}
				}
				else if(group.getVersionID() == destVersion && group.getCGID() == Integer.parseInt(cgMap.destCGInfo.id)){
					group.setSrcGroupMapping(gDestMap);
					for(CloneFragment fragment : group.getClonefragment()){
						for(FragmentMapping fraMap : fMapList){
							if(fragment.getCFID() == fraMap.getDestCFid()){
								fragment.setSrcFragmentMapping(fMap);
							}
						}
					}
				}
			}			
		}
	}*/
	
	public void SaveMappingForSys(int srcVersion,int destVersion){
		GroupMapping gMap=null;
		for (CloneGroupMapping cgMap : this.CGMapList){
			gMap = new GroupMapping();
			gMap.setSrcVersionID(srcVersion);
			gMap.setDestVersionID(destVersion);
			gMap.setSrcCGID(Integer.parseInt(cgMap.srcCGInfo.id));
			gMap.setDestCGID(Integer.parseInt(cgMap.destCGInfo.id));
			gMap.setSrcCGSize(cgMap.srcCGInfo.size);
			gMap.setDestCGSize(cgMap.destCGInfo.size);
			
			List<FragmentMapping> fMapList = new ArrayList<FragmentMapping>();
			FragmentMapping fMap=null;
			for(int k=0;k<cgMap.CFMapList.size();k++){
				fMap = new FragmentMapping();
				fMap.setSrcCFid(Integer.parseInt(((CloneFragmentMapping)cgMap.CFMapList.get(k)).SrcCFID));
				fMap.setDestCFid(Integer.parseInt(((CloneFragmentMapping)cgMap.CFMapList.get(k)).DestCFID));
				fMap.setCRDMatchLevel((((CloneFragmentMapping)cgMap.CFMapList.get(k)).CrdMatchLevel).toString());
				fMap.setTextSim(((CloneFragmentMapping)cgMap.CFMapList.get(k)).textSim);
				
				fMapList.add(fMap);
			}
			gMap.setFragMapList(fMapList);
			
			EvolutionPattern evoPattern = new EvolutionPattern();
			evoPattern.setSTATIC(cgMap.EvoPattern.isSTATIC());
			evoPattern.setSAME(cgMap.EvoPattern.isSAME());
			evoPattern.setADD(cgMap.EvoPattern.isADD());
			evoPattern.setSUBSTRACT(cgMap.EvoPattern.isSUBSTRACT());
			evoPattern.setCONSISTENTCHANGE(cgMap.EvoPattern.isCONSISTENTCHANGE());
			evoPattern.setINCONSISTENTCHANGE(cgMap.EvoPattern.isINCONSISTENTCHANGE());
			evoPattern.setSPLIT(cgMap.EvoPattern.isSPLIT());
			if(cgMap.EvoPattern.getMapGroupIDs() != null){
				evoPattern.setMapGroupIDs(String.valueOf(cgMap.EvoPattern.getMapGroupIDs()));
			}
			gMap.setEvolutionPattern(evoPattern);
			
			VariationInformation.mappingInfo.add(gMap);//���뵽ȫ�ֱ���
			
			//��map���뵽��Ӧ��Group��
			for(CloneGroup group : VariationInformation.cloneGroup){
				if(group.getVersionID() == srcVersion && group.getCGID() == Integer.parseInt(cgMap.srcCGInfo.id)){
					group.setDestGroupMapping(gMap);
					for(CloneFragment fragment : group.getClonefragment()){
						for(FragmentMapping fraMap : gMap.getFragMapList()){
							if(fragment.getCFID() == fraMap.getSrcCFid()){
								fragment.setDestFragmentMapping(fraMap);//fMap.getSrcCFid ������ fragment.getCFID()
							}
						}
					}
				}
				else if(group.getVersionID() == destVersion && group.getCGID() == Integer.parseInt(cgMap.destCGInfo.id)){
					group.setSrcGroupMapping(gMap);
					for(CloneFragment fragment : group.getClonefragment()){
						for(FragmentMapping fraMap : gMap.getFragMapList()){
							if(fragment.getCFID() == fraMap.getDestCFid()){
								fragment.setSrcFragmentMapping(fraMap);
							}
						}
					}
				}
			}
		}
		
		if(this.UnMappedSrcCGList != null){
			for(int k=0;k<this.UnMappedSrcCGList.size();k++){
				gMap = new GroupMapping();
				gMap.setSrcVersionID(srcVersion);
				gMap.setSrcCGID(Integer.parseInt(((CGInfo)this.UnMappedSrcCGList.get(k)).id));
				gMap.setSrcCGSize(((CGInfo)this.UnMappedSrcCGList.get(k)).size);
				
				VariationInformation.unMappedSrcInfo.add(gMap); 
				//System.out.println("unMappedSrcCGVersion " + srcVersion + " CGID " + gMap.getSrcCGID());
			}
		}

		
		if(this.UnMappedDestCGList != null){
			for(int k=0;k<this.UnMappedDestCGList.size();k++){
				gMap = new GroupMapping();
				gMap.setDestVersionID(destVersion);
				gMap.setDestCGID(Integer.parseInt(((CGInfo)this.UnMappedDestCGList.get(k)).id));
				gMap.setDestCGSize(((CGInfo)this.UnMappedDestCGList.get(k)).size);
				
				VariationInformation.unMappedDestInfo.add(gMap);
				//System.out.println("unMappeddestCGVersion" + gMap.getDestVersionID() + " CGID " + gMap.getDestCGID());
			}
		}
				
	}
	
}
