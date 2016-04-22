package PreProcess;

import java.util.ArrayList;
import java.util.List;

import CloneRepresentation.CloneGenealogy;
import CloneRepresentation.GenealogyEvolution;
import CloneRepresentation.GroupMapping;
import Global.VariationInformation;

class CGMapInfo{//�����¡��ϵ�б����CGMap��Ϣ
    public String id;   //id��ʵ�Ƿ���map�ļ���CGMapԪ�صġ�ָ�롱
    public String pattern;
}

public class CreateGenealogyInfo {
	
	public List<GenealogyEvolution> evoGeneaList;
	public int[] evoPatternCount;
	private int id = 0;
	private int endIndex = -1;
	
	/*private boolean IsMappingVersionSuccessive() {
		for (GroupMapping mapping : VariationInformation.mappingInfo) {
			if (mapping.getDestVersionID() - mapping.getSrcVersionID() != 1)
				return false;
		}
		return true;
	}*/
	
	public void CreateGenealogyForAll(){
		/*if(!this.IsMappingVersionSuccessive()){
			System.out.println("�汾������������...");
			return;
		}*/
        //Ѱ��ÿ���汾���²�����CG������Ϊ��������¡��ϵ
		int prev = 0; // ��¼ǰһ���������CG��id�������ظ����죨��Է��ѵ������	
		for (GroupMapping mapping : VariationInformation.mappingInfo) {
			if (mapping.getSrcVersionID() == 0) {// ��һ����ΪԴ�汾�����п�¡Ⱥ��������Ϊ���Ŀ�¡��ϵ
				
				if (mapping.getSrcCGID() != prev) {

					CloneGenealogy cloneGenealogy = new CloneGenealogy();
					cloneGenealogy.setStartVersion(0);// ��ϵ��ʼ�汾
					cloneGenealogy.setRootCGid(mapping.getSrcCGID());// ����¡Ⱥid

					endIndex = -1;
					id = 0;
					evoGeneaList = new ArrayList<GenealogyEvolution>();// ���������ϵ�б�
					this.BuildGenealogyEvolution(mapping);

					cloneGenealogy.setEndVersion(cloneGenealogy.getStartVersion() + endIndex);// ��ֹ�汾
					cloneGenealogy.setAge(endIndex + 1);// ����

					// ͳ�Ƹ��ֽ���ģʽ���ֵĴ�����������evoPatternCount������
					this.evoPatternCount = new int[7];
					for (GenealogyEvolution evolution : this.evoGeneaList) {
						if (evolution.getCgPattern().contains("STATIC")) {
							this.evoPatternCount[0]++;
						}
						if (evolution.getCgPattern().contains("SAME")) {
							this.evoPatternCount[1]++;
						}
						if (evolution.getCgPattern().contains("ADD")) {
							this.evoPatternCount[2]++;
						}
						if (evolution.getCgPattern().contains("DELETE")) {
							this.evoPatternCount[3]++;
						}
						if (!evolution.getCgPattern().contains("INCONSISTENTCHANGE") && evolution.getCgPattern().contains("CONSISTENTCHANGE")) {
							this.evoPatternCount[4]++;
						}
						if (evolution.getCgPattern().contains("INCONSISTENTCHANGE")) {
							this.evoPatternCount[5]++;
						}
						if (evolution.getCgPattern().contains("SPLIT")) {
							this.evoPatternCount[6]++;
						}
					}
					cloneGenealogy.setEvoPatternCount(evoPatternCount);// �������ģʽ���ִ���
					cloneGenealogy.setEvolutionList(evoGeneaList);// ���������ϵ�б�
					prev = mapping.getSrcCGID();
					VariationInformation.cloneGenealogy.add(cloneGenealogy);
				}
			}
		}
		
        //��һ���汾��ûӳ���ϵ�Src��¡��
        if(VariationInformation.unMappedSrcInfo.size() != 0){
            for(GroupMapping singleMapping : VariationInformation.unMappedSrcInfo){
            	if(singleMapping.getSrcVersionID() == 0){
            		CloneGenealogy singleCG = new CloneGenealogy();
            		singleCG.setStartVersion(singleMapping.getSrcVersionID());
            		singleCG.setRootCGid(singleMapping.getSrcCGID());
            		System.out.println("��һ���汾ûӳ���Single" + singleMapping.getSrcVersionID());
            		VariationInformation.singleCgGenealogyList.add(singleCG);
            	}
            }
        }
      
        //���а汾��ûӳ���ϵ�dest��¡��
        if(VariationInformation.unMappedDestInfo.size() != 0){
        	/*	for(GroupMapping destMapping : VariationInformation.unMappedDestInfo){
        		System.out.println("�汾   " + destMapping.getDestVersionID() + "   ��ID " + destMapping.getDestCGID() + "   ��Size " + destMapping.getDestCGSize());
        	}
        	*/
        	for(GroupMapping destMapping : VariationInformation.unMappedDestInfo){
        		CloneGenealogy cloneGenealogy = new CloneGenealogy();
            	cloneGenealogy.setStartVersion(destMapping.getDestVersionID());//��ϵ��ʼ�汾
            	cloneGenealogy.setRootCGid(destMapping.getDestCGID());//����¡Ⱥid
            	
            	endIndex = -1;
            	id = 0;
            	evoGeneaList = new ArrayList<GenealogyEvolution>();//���������ϵ�б�
            	this.BuildGenealogyEvolution(destMapping);
            	
            	if(this.endIndex > 0){
            		cloneGenealogy.setEndVersion(cloneGenealogy.getStartVersion() + endIndex);//��ֹ�汾
                	cloneGenealogy.setAge(endIndex+1);//����           	
                	
                	//ͳ�Ƹ��ֽ���ģʽ���ֵĴ�����������evoPatternCount������
                    this.evoPatternCount = new int[7];
                    for (GenealogyEvolution evolution : this.evoGeneaList){
                        if (evolution.getCgPattern().contains("STATIC") )
                        { this.evoPatternCount[0]++; }
                        if (evolution.getCgPattern().contains("SAME"))
                        { this.evoPatternCount[1]++; }
                        if (evolution.getCgPattern().contains("ADD"))
                        { this.evoPatternCount[2]++; }
                        if (evolution.getCgPattern().contains("DELETE"))
                        { this.evoPatternCount[3]++; }
                        if (!evolution.getCgPattern().contains("INCONSISTENTCHANGE") && evolution.getCgPattern().contains("CONSISTENTCHANGE"))
                        { this.evoPatternCount[4]++; }
                        if (evolution.getCgPattern().contains("INCONSISTENTCHANGE"))
                        { this.evoPatternCount[5]++; }
                        if (evolution.getCgPattern().contains("SPLIT"))
                        { this.evoPatternCount[6]++; }
                    }
                    cloneGenealogy.setEvoPatternCount(evoPatternCount);//�������ģʽ���ִ���
                	cloneGenealogy.setEvolutionList(evoGeneaList);//���������ϵ�б�     	
                	VariationInformation.cloneGenealogy.add(cloneGenealogy);
            	} 	
            	else{
            		//System.out.println("I'm �����汾ûӳ��� SingleGenealogy  " + cloneGenealogy.getStartVersion() + "  " + cloneGenealogy.getRootCGid());
            		VariationInformation.singleCgGenealogyList.add(cloneGenealogy);
            	}
        	}
        }

	}
        	
    // ������ָ��CGΪ���Ŀ�¡��ϵ
    public void BuildGenealogyEvolution(GroupMapping mapping){
    	GenealogyEvolution gEvolu = new GenealogyEvolution ();
    	id = gEvolu.BuildFromCGMap(mapping, id, evoGeneaList);
    	endIndex++;
    	evoGeneaList.add(gEvolu);//���������ϵ�б�
    	List<GroupMapping> targetMapping = new ArrayList<GroupMapping>();
    	for(GroupMapping tar : VariationInformation.mappingInfo){
    		if((mapping.getDestVersionID() == tar.getSrcVersionID()) && 
    				(mapping.getDestCGID() == tar.getSrcCGID())){
    			targetMapping.add(tar);
    		}
    	}
    	if(targetMapping.size() > 0){
    		for(GroupMapping tar : targetMapping){
    			this.BuildGenealogyEvolution(tar);
    		}	
    	}
    	return;
    }
}
