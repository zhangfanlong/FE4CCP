package ExtractFeatures;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import CloneRepresentation.CloneFragment;
import CloneRepresentation.CloneGenealogy;
import CloneRepresentation.CloneGroup;
import CloneRepresentation.GroupMapping;
import Global.Path;
import Global.VariationInformation;
import PreProcess.CreateCRDInfo;
import PreProcess.Diff;
import PreProcess.LevenshteinDistance;

public class CreateTrainedFeatureVector {
	public FeatureVector featureVector;
	//private int consistCount;
	
	public void ExtractFeature(){
		VariationInformation.featureVectorList = new ArrayList<FeatureVector>();
		HalsteadMetric.InitHalsteadParam();
		List<CloneFragment> tarFragment;	
		
		//��Ե�һ���汾
		for(CloneGroup group : VariationInformation.cloneGroup){
			if(group.getVersionID() == 0 /*&& !isSingleGenealogy(0,group.getCGID())*/){
				int consistency = ExtractConsistencyLabel(0,group.getCGID());
				int p=0;
				//for(int p=0;p<group.getClonefragment().size()-1;p++){	
					this.featureVector = new FeatureVector();
					ExtractCodeAndStrucFeature(group.getClonefragment().get(p));//����ͽṹ����
					
					for(int q=p+1;q<group.getClonefragment().size();q++){
						ExtractDestinationFeature(group.getClonefragment().get(p),group.getClonefragment().get(q));//����������
						
						if(group.getClonefragment().size() == 2)//����Ƭ�μ����ƶ�
							this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
						else{
							//�����ı����ƶ� ��С��Similarity��ȡSimilarity
							float similarity = FragmentSimlarity(group.getClonefragment().get(p),group.getClonefragment().get(q));
							if(similarity*100 >= group.getSimilarity())	
								this.featureVector.setSimCloneFragment(similarity);
							else	this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
						}
						//this.featureVector.setConsisCount(this.consistCount);
						this.featureVector.setConsistence(consistency);
						VariationInformation.featureVectorList.add(featureVector);
					}
				//}
			}
		}
	
		//��������汾���²����Ŀ�¡��
		if(VariationInformation.unMappedDestInfo.size()!=0){
			for(GroupMapping unMappedGroup:VariationInformation.unMappedDestInfo){
				//if(!isSingleGenealogy(unMappedGroup.getDestVersionID(),unMappedGroup.getDestCGID())){
					int consistency = ExtractConsistencyLabel(unMappedGroup.getDestVersionID(),unMappedGroup.getDestCGID());
					for(CloneGroup group : VariationInformation.cloneGroup){
						if(group.getVersionID() == unMappedGroup.getDestVersionID() && group.getCGID() == unMappedGroup.getDestCGID()){
							File subSysDir = new File(Global.Path._subSysDirectory);
							String preVersion = subSysDir.list()[group.getVersionID()-1];//��һ�汾��ϵͳ��
							float [] Sim = new float[group.getClonefragment().size()+1];
							//��group����ԭʼƬ��
							for(CloneFragment clonefragment : group.getClonefragment()){	

								String preVerPath = Global.Path._subSysDirectory + "\\" + preVersion + "\\"  + clonefragment.getPath().substring(clonefragment.getPath().indexOf("/")+1);//���°汾��ͬ�������
								String clonePath =  Global.Path._subSysDirectory + "\\" + clonefragment.getPath();//��¡����������·��
								
								List<String> cloneInSource = new ArrayList<String>();
								cloneInSource = CreateCRDInfo.GetFileContent(clonePath);
								if(new File(preVerPath).exists()){//��һ�汾����
									//�ı��Ƚ�  ������һ�汾�Ŀ�¡����
									Sim[clonefragment.getCFID()]=this.FindFragmentInPreVersion(CreateCRDInfo.GetCFSourceFromCRDInfo(cloneInSource, clonefragment.getStartLine(), clonefragment.getEndLine()),
											CreateCRDInfo.GetFileContent(preVerPath));
									if( Sim[clonefragment.getCFID()] < 0.7)	//ʹ�ÿ�¡��⹤��Ĭ�����ƶ�
										{ Sim[clonefragment.getCFID()] = 0; }
								}
								else Sim[clonefragment.getCFID()] = 0;
							}
							//�ҳ�simֵ���Ŀ�¡Ƭ����Ϊ��ԭʼ���ƵĴ���Ƭ��
							float SimMax = 0; 
							int copiedID = 0;
							for(int s=1;s<Sim.length;s++){//clonefragment ID��1��ʼ
								if(Sim[s]>SimMax){
									SimMax = s;
									copiedID = s;
								}
							}
							if(copiedID == 0){ //û���ҵ�ԭʼ�ĸ���Ƭ��
								//for(int p=0;p<group.getClonefragment().size()-1;p++){
									int p=0;
									this.featureVector = new FeatureVector();
									ExtractCodeAndStrucFeature(group.getClonefragment().get(p));//����ͽṹ����			
									for(int q=p+1;q<group.getClonefragment().size();q++){
										ExtractDestinationFeature(group.getClonefragment().get(p),group.getClonefragment().get(q));//����������
										
										if(group.getClonefragment().size() == 2)//����Ƭ�μ����ƶ�
											this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
										else{
											//�����ı����ƶ� ��С��Similarity��ȡSimilarity
											float similarity = FragmentSimlarity(group.getClonefragment().get(p),group.getClonefragment().get(q));
											if(similarity*100 >= group.getSimilarity())	
												this.featureVector.setSimCloneFragment(similarity);
											else	this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
										}
										//this.featureVector.setConsisCount(this.consistCount);
										this.featureVector.setConsistence(consistency);
										VariationInformation.featureVectorList.add(featureVector);
									}
								//}
							}
							else{
								CloneFragment copiedFrag=null;
								tarFragment = new ArrayList<CloneFragment>();
								for(CloneFragment frag : group.getClonefragment()){
									if(frag.getCFID() == copiedID){
										copiedFrag = frag;
									}	
									else tarFragment.add(frag);
								}
								
								this.featureVector = new FeatureVector();
								ExtractCodeAndStrucFeature(copiedFrag);//����ͽṹ����
								
								for(CloneFragment frag2 : tarFragment){
									ExtractDestinationFeature(copiedFrag,frag2);//����������
									
									if(group.getClonefragment().size() == 2)//����Ƭ�μ����ƶ�
										this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
									else{
										//�����ı����ƶ� ��С��Similarity��ȡSimilarity
										float similarity = FragmentSimlarity(copiedFrag,frag2);
										if(similarity*100 >= group.getSimilarity())	
											this.featureVector.setSimCloneFragment(similarity);
										else	this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
									}
									//this.featureVector.setConsisCount(this.consistCount);
									this.featureVector.setConsistence(consistency);
									VariationInformation.featureVectorList.add(featureVector);
								}
								
							}//else			
						}
					}
				//}
			}
		}//if
		
		WekaOperations.WriteFeaturesToArff(Path._clonesFolderPath + "FeatureVector.arff",1);
	}

	/*private boolean isSingleGenealogy(int version,int cgID){
		for(CloneGenealogy single : VariationInformation.singleCgGenealogyList){
			if(single.getStartVersion() == version && single.getRootCGid() == cgID)
				return true;
		}
		return false;
	} */
	
	private void ExtractCodeAndStrucFeature(CloneFragment frag){
		
		this.featureVector.setSourceLine(frag.getEndLine() - frag.getStartLine()+1);//��������
		String subSysPath = Path._subSysDirectory + "\\" + frag.getPath();
		List<String> cloneCode = FeatureOperations.GetCodesFromFile(subSysPath, frag.getStartLine(), frag.getEndLine());
    
		
		//�������ô�������������AST��ȡ
  		CompilationUnit cu = FeatureOperations.CreateAST(subSysPath);	
  		int startPos = cu.getPosition(frag.getStartLine(), 0);
  		int endPos = cu.getPosition(frag.getEndLine() + 1,0 ); //endLine + 1 or startPos + 1
  		
  		String className = frag.getCRD().getClassName();//��ÿ�¡������������,��λ����Ϣ
  		MethodInvocCountVisitor invocFeatureVisitor = new MethodInvocCountVisitor(startPos,endPos,className,VariationInformation.allVersionJavaFiles.get(frag.getPath().substring(0, frag.getPath().indexOf("/"))));
  		cu.accept(invocFeatureVisitor);
  		this.featureVector.setTotalParameterCount(invocFeatureVisitor.getTotalParameterCount());
  		this.featureVector.setTotalMethodInvocCount(invocFeatureVisitor.getTotalMethodInvocCount());
  		this.featureVector.setLocalMethodInvocCount(invocFeatureVisitor.getLocalMethodInvocCount());
  		this.featureVector.setLibraryMethodInvocCount(invocFeatureVisitor.getLibraryMethodInvocCount());
  		this.featureVector.setOtherMethodInvocCount(invocFeatureVisitor.getOtherMethodInvocCount());
  	
  		
  		//halstead����  
        HalsteadMetric halMetric = FeatureOperations.GetHalsteadInfo(cloneCode);
        this.featureVector.setUniOPERATORCount(halMetric.getUniOPERATORCount());
        this.featureVector.setUniOperandCount(halMetric.getUniOperandCount());
        this.featureVector.setTotalOPERATORCount(halMetric.getTotalOPERATORCount());
        this.featureVector.setTotalOperandCount(halMetric.getTotalOperandCount());         
  		
        
        //�ṹ����
        StructuralFeatureVisitor strucFeatureVisitor = FeatureOperations.GetStructuralInfo(cu, startPos, endPos, false);
        this.featureVector.setStruFeature(strucFeatureVisitor.getStructuralFeature());
        //ExtractStructuralFeature(cu,startPos,endPos);
	}
	
/*	private void ExtractStructuralFeature(CompilationUnit cu,int startPos,int endPos){
		StructuralFeatureVisitor strucFeatureVisitor = new StructuralFeatureVisitor(startPos,endPos);
        cu.accept(strucFeatureVisitor);
        this.featureVector.setStruFeature(strucFeatureVisitor.getStructuralFeature());
	}*/
	
	private void ExtractDestinationFeature(CloneFragment frag,CloneFragment frag2){//fragΪ���Ƶ�Ƭ��,frag2Ϊճ����Ƭ��
		//�Ƿ��Ǿֲ���¡
		this.featureVector.setLocalClone(frag.getPath().equals(frag2.getPath()));
		//�ļ��������ƶ�
		this.featureVector.setSimFileName(LevenshteinDistance.sim(frag.getFileName(), frag2.getFileName()));
		//Masked �ļ������ƶ�
		if(featureVector.isLocalClone()) this.featureVector.setSimMaskedFileName(0);
		else this.featureVector.setSimMaskedFileName(1);		
		
		
		float maxSimParaName = -1;//�����������ƶ�
		float sumSimParaName = 0;//������
		float sumSimParaType = 0;//��������
		//���������ƶ�
		//��¡���벻��һ����������,�����ڷ��������ƶ�Ϊ1������������������������Ӧ�ÿ���?????
		if(frag.getCRD().getMethodInfo()==null  && frag2.getCRD().getMethodInfo()==null){
			this.featureVector.setSimMethodName(1);
			
			this.featureVector.setSimTotalParaName(1);
			this.featureVector.setSimTotalParaType(1);
			this.featureVector.setSimMaxParaName(1);
		} else if((frag.getCRD().getMethodInfo()==null  && frag2.getCRD().getMethodInfo()!=null) ||
				(frag.getCRD().getMethodInfo()!=null  && frag2.getCRD().getMethodInfo()==null)){
			this.featureVector.setSimMethodName(0);
			
			this.featureVector.setSimTotalParaName(0);
			this.featureVector.setSimTotalParaType(0);
			this.featureVector.setSimMaxParaName(0);
		} else { 
			this.featureVector.setSimMethodName(LevenshteinDistance.sim(frag.getCRD().getMethodInfo().mName, frag2.getCRD().getMethodInfo().mName));
			//����������ƶ�
			if(frag.getCRD().getMethodInfo().mParaNum == 0 && frag2.getCRD().getMethodInfo().mParaNum == 0){
				this.featureVector.setSimTotalParaName(1);
				this.featureVector.setSimTotalParaType(1);
				this.featureVector.setSimMaxParaName(1);
			}
			else if((frag.getCRD().getMethodInfo().mParaNum == 0 && frag2.getCRD().getMethodInfo().mParaNum != 0) ||
					(frag.getCRD().getMethodInfo().mParaNum != 0 && frag2.getCRD().getMethodInfo().mParaNum == 0)){
				this.featureVector.setSimTotalParaName(0);
				this.featureVector.setSimTotalParaType(0);
				this.featureVector.setSimMaxParaName(0);
			}
			else{  
				for(int m=0;m<frag.getCRD().getMethodInfo().mParaTypeList.size();m++){
					String strName1 = frag.getCRD().getMethodInfo().mParaNameList.get(m);
					String strType1 = frag.getCRD().getMethodInfo().mParaTypeList.get(m);
					for(int n=0;n<frag2.getCRD().getMethodInfo().mParaTypeList.size();n++){
						String strName2 = frag2.getCRD().getMethodInfo().mParaNameList.get(n);
						String strType2 = frag2.getCRD().getMethodInfo().mParaTypeList.get(n);
						
						sumSimParaType += LevenshteinDistance.sim(strType1, strType2);
						sumSimParaName += LevenshteinDistance.sim(strName1, strName2);
						if(LevenshteinDistance.sim(strName1, strName2) > maxSimParaName) 
							maxSimParaName = LevenshteinDistance.sim(strName1, strName2);
					}
				}
				//�ܵĲ������ƶ�
				this.featureVector.setSimTotalParaName(sumSimParaName);
				this.featureVector.setSimTotalParaType(sumSimParaType);
				this.featureVector.setSimMaxParaName(maxSimParaName);
			}
			
		}
			
		
		
		
		//.........................����Ϣ����ô��????????????????
		if(frag.getCRD().getBlockInfos() == null && frag2.getCRD().getBlockInfos() == null){
			featureVector.setIsSameBlockInfo(true);
		} else if((frag.getCRD().getBlockInfos() == null && frag2.getCRD().getBlockInfos() != null) ||
				(frag.getCRD().getBlockInfos() != null && frag2.getCRD().getBlockInfos() == null)){
			featureVector.setIsSameBlockInfo(false);
		} else if(CreateCRDInfo.compareWithBlockList(frag.getCRD().getBlockInfos(),frag2.getCRD().getBlockInfos())){
			featureVector.setIsSameBlockInfo(true);
		} else{featureVector.setIsSameBlockInfo(false);}
	}
	
	private int ExtractConsistencyLabel(int versionId,int groupId){
	//	this.consistCount = 0;
		for(CloneGenealogy cloneGene : VariationInformation.cloneGenealogy){
			if(cloneGene.getStartVersion() == versionId && cloneGene.getRootCGid() == groupId){
				if(cloneGene.getEvoPatternCount()[4] > 0){ //������һ�±仯����Ϊ����Ҫһ��ά��,��θĽ�??????????????
				//	this.consistCount += cloneGene.getEvoPatternCount()[4];
					return 1; 
				}
				else return 0;
			}
		}
		//���汾��¡��ϵ ����Ϊ�ǲ���Ҫһ����ά����
		return 0;
	}
	
	public float FindFragmentInPreVersion (List<String> tarfragment,List<String> srcfragment){
		Diff.UseDefaultStrSimTh();  //ʹ�������ƶ���ֵĬ��ֵ0.5
		return new Diff().FineFragment(tarfragment, srcfragment);
	}

	
	private float FragmentSimlarity(CloneFragment frag,CloneFragment frag2){//fragΪ���Ƶ�Ƭ��,frag2Ϊճ����Ƭ��
		
		String subSysPath = Path._subSysDirectory + "\\" + frag.getPath();
		List<String> sourceCode=CreateCRDInfo.GetFileContent(subSysPath);
		List<String> cloneCode = new ArrayList<String>();
		cloneCode =	CreateCRDInfo.GetCFSourceFromCRDInfo(sourceCode, frag.getStartLine(), frag.getEndLine());
		
		String subSysPath2 = Path._subSysDirectory + "\\" + frag2.getPath();
		List<String> sourceCode2=CreateCRDInfo.GetFileContent(subSysPath2);
		List<String> cloneCode2 = new ArrayList<String>();
		cloneCode2 = CreateCRDInfo.GetCFSourceFromCRDInfo(sourceCode2, frag2.getStartLine(), frag2.getEndLine());

		Diff.UseDefaultStrSimTh();  //ʹ�������ƶ���ֵĬ��ֵ0.5
		return Diff.FileSimilarity(new Diff().DiffFiles(cloneCode, cloneCode2), 
				cloneCode.size(), cloneCode2.size(), true);
		
	}
	
}
