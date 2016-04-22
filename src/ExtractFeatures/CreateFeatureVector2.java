package ExtractFeatures;

import java.io.BufferedInputStream;
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
import CloneRepresentation.GenealogyEvolution;
import Global.Path;
import Global.VariationInformation;
import PreProcess.CreateCRDInfo;
import PreProcess.Diff;
import PreProcess.LevenshteinDistance;

public class CreateFeatureVector2 { //Ԥ����Ͻ�������
	public FeatureVector featureVector;
	private int consist;
	private int[] pattern;
	private int age;
	
	//����Ƭ���ܵ�
	private int souceLines;
	private int fragCount;
	private int paraCount;
	private int totalMethodInvocCount;
	private int localMethodInvocCount;
	private int libraryMethodInvocCount;
	private int otherMethodInvocCount;
	private int uniOPERATORCount;
	private int uniOperandCount;
	private int totalOPERATORCount;
	private int totalOperandCount;
	private int[] structuralFeature;
	
	//����������
	private boolean isLocal;
	private float simFileName;
	private float simMethodName;
	private float simTotalParaName;
	private float simTotalParaType;
	private boolean isSameBlockInfo;
	private float simMaxParaName;
	
	private void init(){
		consist=0;
		age = 0;
		souceLines = 0;
		fragCount = 0;
		paraCount = 0;
		totalMethodInvocCount = 0;
		localMethodInvocCount = 0;
		libraryMethodInvocCount = 0;
		otherMethodInvocCount = 0;
		uniOPERATORCount = 0;
		uniOperandCount = 0;
		totalOPERATORCount = 0;
		totalOperandCount = 0;
		
		structuralFeature = new int[RelatedNodes.relevantNode.values().length];
		for(int i=0;i<structuralFeature.length;i++)
			structuralFeature[i] = 0;

		
		isLocal = true;
		simFileName = 0;
		simMethodName=0;
		simTotalParaName=0;
		simTotalParaType=0;
		isSameBlockInfo = true;
		simMaxParaName=-1;//�����������ƶ�
	}
	
	public void ExtractFeature(){
		VariationInformation.featureVectorList = new ArrayList<FeatureVector>(); 
		
		HalsteadMetric.InitHalsteadParam();
		
		for(CloneGenealogy cloneGene : VariationInformation.cloneGenealogy){
			
			List<GenealogyEvolution> genEvoList = cloneGene.getEvolutionList();
			
			for(GenealogyEvolution evo : genEvoList){
				if(evo.getCgPattern().contains("CONSISTENTCHANGE") || evo.getCgPattern().contains("INCONSISTENTCHANGE")){
					//��dest��ȡ
					CloneGroup group = new CloneGroup();
					//group = FindCLoneGroup(evo.getDestVersion(),evo.getDestCGID()); //��ǰ�汾��ȡ
					group = FindCLoneGroup(evo.getSrcVersion(),evo.getSrcCGID()); //��һ�汾��ȡ
					this.featureVector = new FeatureVector();
					
					ExtractForGroup(group);//��ȡ�����仯�Ŀ�¡������
					
					//�ж�ģʽ��һ��������
					if(!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
							evo.getCgPattern().contains("CONSISTENTCHANGE")) {consist = 1;}
					else if(evo.getCgPattern().contains("INCONSISTENTCHANGE")) {
						consist = 0;
						while(evo.getChildID()!=null){
							for(GenealogyEvolution tempEvo : genEvoList){
								if(tempEvo.getID().equals(evo.getChildID())){
									evo = tempEvo;
									break;
								}
							}
							
							if(!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
									evo.getCgPattern().contains("CONSISTENTCHANGE")) {
								consist = 1;
								break;
							}
						}
					}
					//labelһ��������
					//�ж�ģʽ��һ��������
					if(!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
							evo.getCgPattern().contains("CONSISTENTCHANGE")) {consist = 1;}
					else if(evo.getCgPattern().contains("INCONSISTENTCHANGE")) {
						consist = 0;
						while(evo.getChildID()!=null){
							for(GenealogyEvolution tempEvo : genEvoList){
								if(tempEvo.getID().equals(evo.getChildID())){
									evo = tempEvo;
									break;
								}
							}
							
							if(!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
									evo.getCgPattern().contains("CONSISTENTCHANGE")) {
								consist = 1;
								break;
							}
						}
					}
					
					/*
					//�ϵ�
					//�ж�ģʽ��һ��������
					if(!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
							evo.getCgPattern().contains("CONSISTENTCHANGE")) consist = 1;
					if(evo.getCgPattern().contains("INCONSISTENTCHANGE")) consist = 0;
					*/
					
					pattern = new int[7];
					while(evo.getParentID()!=null){
						
						for(GenealogyEvolution tempEvo : genEvoList){
							if(tempEvo.getID().equals(evo.getParentID())){
								evo = tempEvo;
								break;
							}
						}
						
						if (evo.getCgPattern().contains("STATIC"))  pattern[0] += 1;
						if (evo.getCgPattern().contains("SAME"))	pattern[1] += 1;
						if (evo.getCgPattern().contains("ADD"))	pattern[2] += 1;
						if (evo.getCgPattern().contains("DELETE")) pattern[3] += 1;
						if (evo.getCgPattern().contains("SPLIT")) pattern[4] += 1;
						
						if (!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
								evo.getCgPattern().contains("CONSISTENTCHANGE")) pattern[5] += 1;
						if (evo.getCgPattern().contains("INCONSISTENTCHANGE")) pattern[6] += 1;


						
						if(evo.getSrcSize() != -1)	++age;//��¡����
					}									
					
					this.featureVector.setConsistence(consist);
					this.featureVector.setAge(age);
					this.featureVector.setEvoPattern(pattern);
					VariationInformation.featureVectorList.add(featureVector);
				}
			}
		}
		
		WekaOperations.WriteFeaturesToArff(Path._clonesFolderPath + "FeatureVector_2.arff",2);
		
	}//ExtractFeature()
	
	private void ExtractForGroup(CloneGroup group){	
		init();
		int length = group.getClonefragment().size();
		this.featureVector.setFragCount(length);
		for(int i=0;i<length;i++){
			//����ͽṹ����
			StatCodeAndStrucFeature(group.getClonefragment().get(i));
		}

		this.featureVector.setSourceLine(souceLines/length);
  		this.featureVector.setTotalParameterCount(paraCount/length);
  		this.featureVector.setTotalMethodInvocCount(totalMethodInvocCount/length);
  		this.featureVector.setLocalMethodInvocCount(localMethodInvocCount/length);
  		this.featureVector.setLibraryMethodInvocCount(libraryMethodInvocCount/length);
  		this.featureVector.setOtherMethodInvocCount(otherMethodInvocCount/length);

        this.featureVector.setUniOPERATORCount(uniOPERATORCount/length);
        this.featureVector.setUniOperandCount(uniOperandCount/length);
        this.featureVector.setTotalOPERATORCount(totalOPERATORCount/length);
        this.featureVector.setTotalOperandCount(totalOperandCount/length);      
        for(int k=0;k<this.structuralFeature.length;k++){
        	structuralFeature[k] = structuralFeature[k]/length;
        }
        this.featureVector.setStruFeature(structuralFeature);
      
        int count=0;
        for(int i=0;i<length-1;i++){
        	for(int j=i+1;j<length;j++){
        		//����������
        		ExtractDestinationFeature(group.getClonefragment().get(i),group.getClonefragment().get(j));
        		count++;
        	}
        }
        
        this.featureVector.setLocalClone(isLocal);
        //�ļ��������ƶ�
        this.featureVector.setSimFileName(simFileName/count);
      	//Masked �ļ������ƶ�
      	if(featureVector.isLocalClone()) this.featureVector.setSimMaskedFileName(0);
      	else this.featureVector.setSimMaskedFileName(1);	

      	this.featureVector.setSimMethodName(simMethodName/count);	
		this.featureVector.setSimTotalParaName(simTotalParaName/count);
		this.featureVector.setSimTotalParaType(simTotalParaType/count);
		
		//�ܵĲ������ƶ�
		this.featureVector.setSimTotalParaName(simTotalParaName/count);
		this.featureVector.setSimTotalParaType(simTotalParaType/count);
		this.featureVector.setSimMaxParaName(simMaxParaName);
		this.featureVector.setIsSameBlockInfo(isSameBlockInfo);
		this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
		
	}
	
	private CloneGroup FindCLoneGroup(int versionID,int CGID){
		for(CloneGroup group : VariationInformation.cloneGroup){
			if(group.getVersionID() == versionID && group.getCGID() == CGID) {
				return group;
			} 
		}
		return null;
	}
	
	private CompilationUnit CreateAST(String subSysPath,String classPath){
		byte[] input =null;
        try {
        	BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(subSysPath));
			input = new byte[bufferedInputStream.available()];  
	        bufferedInputStream.read(input);  
	        bufferedInputStream.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ASTParser astParser = ASTParser.newParser(AST.JLS8);  
		astParser.setSource(new String(input).toCharArray());
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		return (CompilationUnit)(astParser.createAST(null));
	}
	
	private void StatCodeAndStrucFeature(CloneFragment frag){
		this.souceLines += (frag.getEndLine() - frag.getStartLine()+1);	//��������

		String subSysPath = Path._subSysDirectory + "\\" + frag.getPath();
		List<String> sourceCode=CreateCRDInfo.GetFileContent(subSysPath);
		List<String> cloneCode = new ArrayList<String>();
		cloneCode = CreateCRDInfo.GetCFSourceFromCRDInfo(sourceCode, frag.getStartLine(), frag.getEndLine());
		
        //�������ô�������������AST��ȡ
		String sysClassFilesPath = Path._subClassFilesDirectory + "\\" + frag.getPath().split("/")[0];
  		CompilationUnit cu = this.CreateAST(subSysPath,sysClassFilesPath);	
  		//��ÿ�¡������������,��λ����Ϣ
  		String className = frag.getCRD().getClassName();
  		int startPos = cu.getPosition(frag.getStartLine(), 0);
  		int endPos = cu.getPosition(frag.getEndLine() + 1,0 ); //endLine + 1 or startPos + 1
  		
  		MethodInvocCountVisitor invocFeatureVisitor = new MethodInvocCountVisitor(startPos,endPos,className,VariationInformation.allVersionJavaFiles.get(frag.getPath().substring(0, frag.getPath().indexOf("/"))));
  		cu.accept(invocFeatureVisitor);
  		
  		this.paraCount += invocFeatureVisitor.getTotalParameterCount();
  		this.totalMethodInvocCount += invocFeatureVisitor.getTotalMethodInvocCount();
  		this.localMethodInvocCount += invocFeatureVisitor.getLocalMethodInvocCount();
  		this.libraryMethodInvocCount += invocFeatureVisitor.getLibraryMethodInvocCount();
  		this.otherMethodInvocCount += invocFeatureVisitor.getOtherMethodInvocCount();
  		
  		//halstead����  
        cloneCode = PreProcess.clearComment(cloneCode);
        cloneCode = PreProcess.clearString(cloneCode);
        cloneCode = PreProcess.clearImport(cloneCode);
        HalsteadMetric halMetric = new HalsteadMetric(cloneCode);
        
        this.uniOPERATORCount += halMetric.getUniOPERATORCount();
        this.uniOperandCount += halMetric.getUniOperandCount();
        this.totalOPERATORCount += halMetric.getTotalOPERATORCount(); 
        this.totalOperandCount += halMetric.getTotalOperandCount();
        
        StatStructuralFeature(cu,startPos,endPos);//�ṹ����
	}
	
	private void StatStructuralFeature(CompilationUnit cu,int startPos,int endPos){
		StructuralFeatureVisitor strucFeatureVisitor = new StructuralFeatureVisitor(startPos,endPos);
        cu.accept(strucFeatureVisitor);
        for(int i =0 ; i<structuralFeature.length ; i++)
        	structuralFeature[i] += strucFeatureVisitor.getStructuralFeature()[i];   
	}
	
	private void ExtractDestinationFeature(CloneFragment frag,CloneFragment frag2){//fragΪ���Ƶ�Ƭ��,frag2Ϊճ����Ƭ��
		//�Ƿ��Ǿֲ���¡
		if(isLocal && !frag.getPath().equals(frag2.getPath()))
			isLocal = false;
		//�ļ��������ƶ�
		simFileName += LevenshteinDistance.sim(frag.getFileName(), frag2.getFileName());		
		
		//���������ƶ�
		//��¡���벻�ڷ�������,�����ڷ��������ƶ�Ϊ1������������������������Ӧ�ÿ���?????
		if(frag.getCRD().getMethodInfo()==null  && frag2.getCRD().getMethodInfo()==null){
			simMethodName += 1;
			simTotalParaName+=1;
			simTotalParaType+=1;
			simMaxParaName = 1;
		} else if((frag.getCRD().getMethodInfo()==null  && frag2.getCRD().getMethodInfo()!=null) ||
				(frag.getCRD().getMethodInfo()!=null  && frag2.getCRD().getMethodInfo()==null)){
			simMaxParaName = 0;
/*			this.featureVector.setSimMethodName(0);
			
			this.featureVector.setSimTotalParaName(0);
			this.featureVector.setSimTotalParaType(0);
			this.featureVector.setSimMaxParaName(0);*/
		} else { 
			simMethodName += LevenshteinDistance.sim(frag.getCRD().getMethodInfo().mName, frag2.getCRD().getMethodInfo().mName);
			//����������ƶ�
			if(frag.getCRD().getMethodInfo().mParaNum == 0 && frag2.getCRD().getMethodInfo().mParaNum == 0){
				simTotalParaName+=1;
				simTotalParaType+=1;
				simMaxParaName = 1;
			}
			else if((frag.getCRD().getMethodInfo().mParaNum == 0 && frag2.getCRD().getMethodInfo().mParaNum != 0) ||
					(frag.getCRD().getMethodInfo().mParaNum != 0 && frag2.getCRD().getMethodInfo().mParaNum == 0)){
				simMaxParaName = 0;
				/*this.featureVector.setSimTotalParaName(0);
				this.featureVector.setSimTotalParaType(0);
				this.featureVector.setSimMaxParaName(0);*/
			}
			else{  
				for(int m=0;m<frag.getCRD().getMethodInfo().mParaTypeList.size();m++){
					String strName1 = frag.getCRD().getMethodInfo().mParaNameList.get(m);
					String strType1 = frag.getCRD().getMethodInfo().mParaTypeList.get(m);
					for(int n=0;n<frag2.getCRD().getMethodInfo().mParaTypeList.size();n++){
						String strName2 = frag2.getCRD().getMethodInfo().mParaNameList.get(n);
						String strType2 = frag2.getCRD().getMethodInfo().mParaTypeList.get(n);
						
						simTotalParaType += LevenshteinDistance.sim(strType1, strType2);
						simTotalParaName += LevenshteinDistance.sim(strName1, strName2);
						if(LevenshteinDistance.sim(strName1, strName2) > simMaxParaName) 
							simMaxParaName = LevenshteinDistance.sim(strName1, strName2);
					}
				}

			}
		}
			
		if(isSameBlockInfo){
			if(frag.getCRD().getBlockInfos() == null && frag2.getCRD().getBlockInfos() == null){
			} else if(((frag.getCRD().getBlockInfos() == null && frag2.getCRD().getBlockInfos() != null) ||
					(frag.getCRD().getBlockInfos() != null && frag2.getCRD().getBlockInfos() == null))){
				isSameBlockInfo = false;
			} else if(!CreateCRDInfo.compareWithBlockList(frag.getCRD().getBlockInfos(),frag2.getCRD().getBlockInfos())){
				isSameBlockInfo = false;
			}
		}
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
