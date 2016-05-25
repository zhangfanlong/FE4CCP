package ExtractFeatures;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
	private int[] numberofpatterns;//�ܵ���ʷ����
	private int[] lastversionofpattern;
		
	
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
	
	
	//��һ�汾
	public FeatureVector last_featureVector;
	//��һ�汾�ĸ��ִ�������
	private int last_souceLines;
	private int last_fragCount;
	private int last_totalParaCount;
	private int last_totalMethodInvocCount;
	private int last_localMethodInvocCount;
	private int last_libraryMethodInvocCount;
	private int last_otherMethodInvocCount;
	private int last_uniOPERATORCount;
	private int last_uniOperandCount;
	private int last_totalOPERATORCount;
	private int last_totalOperandCount;
	private int[] last_structuralFeatureP;
	private int[] last_structuralFeatureN;	
	//private int[] structuralFeatureChanges_fromOrigin;
	
	private void last_init(){
		last_souceLines=0;
		last_fragCount=0;
		last_totalParaCount=0;
		last_totalMethodInvocCount=0;
		last_localMethodInvocCount=0;
		last_libraryMethodInvocCount=0;
		last_otherMethodInvocCount=0;
		last_uniOPERATORCount=0;
		last_uniOperandCount=0;
		last_totalOPERATORCount=0;
		last_totalOperandCount=0;
		
		last_structuralFeatureP = new int[RelatedNodes.relevantNode.values().length];
		last_structuralFeatureN = new int[RelatedNodes.relevantNode.values().length];
		//structuralFeatureChanges_fromOrigin = new int[RelatedNodes.relevantNode.values().length];
		for(int i=0;i<structuralFeature.length;i++){
			last_structuralFeatureP[i]=0;
			last_structuralFeatureN[i]=0;
			//structuralFeatureChanges_fromOrigin[i] = 0;
		}
		
	}
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
		for(int i=0;i<structuralFeature.length;i++){
			structuralFeature[i] = 0;
		}
		
		isLocal = true;
		simFileName = 0;
		simMethodName = 0;
		simTotalParaName = 0;
		simTotalParaType = 0;
		isSameBlockInfo = true;
		simMaxParaName = -1;// �����������ƶ�
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

					//labelһ���������ж�ģʽ��һ��������
					//����next version��future version�����ֱ��
					//label_1����future version
					//label_2����next version
					
					//label_1����future version					
					GenealogyEvolution TempEvo = evo;
					if(!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
							evo.getCgPattern().contains("CONSISTENTCHANGE")) {
						consist = 1;}
					else  {
						consist = 0;
						Stack<String> stack=new Stack<String>();
						while(evo.getChildID()!=null){
							String childID = evo.getChildID();
							if(!childID.contains("+")){
								stack.push(evo.getChildID());
							} else {//���Ƿ���ģʽ
								String [] child_ID = childID.split("\\+");
								for(String child : child_ID){
									stack.push(child);
								}
							}
							while(!stack.empty()){
								for(GenealogyEvolution tempEvo : genEvoList){
									if(tempEvo.getID().equals(stack.peek())){
										evo = tempEvo;
										stack.pop();
										if(evo.getChildID()!=null) {
											if(!evo.getChildID().contains("+")){
												stack.push(evo.getChildID());
												break;
											} else {
												for(String child_id:evo.getChildID().split("\\+")){
													stack.push(child_id);
												}
												break;
											}
										}
										break;
									}
								} 
								
								if(!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
										evo.getCgPattern().contains("CONSISTENTCHANGE")) {
									consist = 1;
									break;
								}
							}
							if(consist==1) break;
						}
					}
					
					evo = TempEvo;
					
					//��ȡj+1�汾
					last_featureVector = new FeatureVector();
					CloneGroup nextVersionGroup = new CloneGroup();
					nextVersionGroup = FindCLoneGroup(evo.getDestVersion(),evo.getDestCGID()); //��һ�汾��ȡevo.getSrcVersion()
					ExtractForLastGroup(nextVersionGroup);
					//����j~j+1�汾�仯��Ϣ
					/*
					 * ����仯
					featureVector.setLast_souceLines(last_featureVector.getSourceLine()-featureVector.getSourceLine());
					featureVector.setLast_fragCount(last_featureVector.getFragCount()-featureVector.getFragCount());
					featureVector.setLast_totalParameterCount(last_featureVector.getTotalParameterCount()-featureVector.getTotalParameterCount());
					featureVector.setLast_totalMethodInvocCount(last_featureVector.getTotalMethodInvocCount()-featureVector.getTotalMethodInvocCount());
					featureVector.setLast_localMethodInvocCount(last_featureVector.getLocalMethodInvocCount()-featureVector.getLocalMethodInvocCount());
					featureVector.setLast_libraryMethodInvocCount(last_featureVector.getLibraryMethodInvocCount()-featureVector.getLibraryMethodInvocCount());
					featureVector.setLast_otherMethodInvocCount(last_featureVector.getOtherMethodInvocCount()-featureVector.getOtherMethodInvocCount());
					featureVector.setLast_uniOPERATORCount(last_featureVector.getUniOPERATORCount()-featureVector.getUniOPERATORCount());
					featureVector.setLast_uniOperandCount(last_featureVector.getUniOperandCount()-featureVector.getUniOperandCount());
					featureVector.setLast_totalOPERATORCount(last_featureVector.getTotalOPERATORCount()-featureVector.getTotalOPERATORCount());
					featureVector.setLast_totalOperandCount(last_featureVector.getTotalOperandCount()-featureVector.getTotalOperandCount());
					for(int l=0 ; l<featureVector.getStruFeature().length ; l++){
						last_structuralFeatureP[l] = last_featureVector.getStruFeature()[l]-featureVector.getStruFeature()[l];//����һ��last_structuralFeatureP����   
					}
					*/
					//�仯����
					if((last_featureVector.getSourceLine()-featureVector.getSourceLine())>0){
						//featureVector.setLast_souceLines(last_featureVector.getSourceLine()-featureVector.getSourceLine());
						featureVector.setLast_souceLines(1);
					}
					else if((last_featureVector.getSourceLine()-featureVector.getSourceLine())<0){
						//featureVector.setLast_souceLines(last_featureVector.getSourceLine()-featureVector.getSourceLine());
						featureVector.setLast_souceLines(-1);
					}else {
						//featureVector.setLast_souceLines(last_featureVector.getSourceLine()-featureVector.getSourceLine());
						featureVector.setLast_souceLines(0);
					}
					
					if ((last_featureVector.getFragCount()-featureVector.getFragCount())>0) {
						//featureVector.setLast_fragCount(last_featureVector.getFragCount()-featureVector.getFragCount());
						featureVector.setLast_fragCount(1);
					} else if((last_featureVector.getFragCount()-featureVector.getFragCount())< 0) {
						//featureVector.setLast_fragCount(last_featureVector.getFragCount()-featureVector.getFragCount());
						featureVector.setLast_fragCount(-1);
					} else {
						//featureVector.setLast_fragCount(last_featureVector.getFragCount()-featureVector.getFragCount());
						featureVector.setLast_fragCount(0);
					}

					if ((last_featureVector.getTotalParameterCount()-featureVector.getTotalParameterCount())> 0) {
						//featureVector.setLast_totalParameterCount(last_featureVector.getTotalParameterCount()-featureVector.getTotalParameterCount());
						featureVector.setLast_totalParameterCount(1);
					} else if ((last_featureVector.getTotalParameterCount()-featureVector.getTotalParameterCount())< 0){
						//featureVector.setLast_totalParameterCount(last_featureVector.getTotalParameterCount()-featureVector.getTotalParameterCount());
						featureVector.setLast_totalParameterCount(-1);
					} else {
						//featureVector.setLast_totalParameterCount(last_featureVector.getTotalParameterCount()-featureVector.getTotalParameterCount());
						featureVector.setLast_totalParameterCount(0);
					}
					
					if ((last_featureVector.getTotalMethodInvocCount()-featureVector.getTotalMethodInvocCount())> 0) {
						//featureVector.setLast_totalMethodInvocCount(last_featureVector.getTotalMethodInvocCount()-featureVector.getTotalMethodInvocCount());
						featureVector.setLast_totalMethodInvocCount(1);
					} else if((last_featureVector.getTotalMethodInvocCount()-featureVector.getTotalMethodInvocCount())< 0)  {
						//featureVector.setLast_totalMethodInvocCount(last_featureVector.getTotalMethodInvocCount()-featureVector.getTotalMethodInvocCount());
						featureVector.setLast_totalMethodInvocCount(-1);
					} else {
						//featureVector.setLast_totalMethodInvocCount(last_featureVector.getTotalMethodInvocCount()-featureVector.getTotalMethodInvocCount());
						featureVector.setLast_totalMethodInvocCount(0);
					}
					
					if ((last_featureVector.getLocalMethodInvocCount()-featureVector.getLocalMethodInvocCount())> 0) {
						//featureVector.setLast_localMethodInvocCount(last_featureVector.getLocalMethodInvocCount()-featureVector.getLocalMethodInvocCount());
						featureVector.setLast_localMethodInvocCount(1);
					} else if ((last_featureVector.getLocalMethodInvocCount()-featureVector.getLocalMethodInvocCount())< 0){
						//featureVector.setLast_localMethodInvocCount(last_featureVector.getLocalMethodInvocCount()-featureVector.getLocalMethodInvocCount());
						featureVector.setLast_localMethodInvocCount(-1);
					}else {
						//featureVector.setLast_localMethodInvocCount(last_featureVector.getLocalMethodInvocCount()-featureVector.getLocalMethodInvocCount());
						featureVector.setLast_localMethodInvocCount(0);
					}
					
					if ((last_featureVector.getLibraryMethodInvocCount()-featureVector.getLibraryMethodInvocCount())> 0) {
						//featureVector.setLast_libraryMethodInvocCount(last_featureVector.getLibraryMethodInvocCount()-featureVector.getLibraryMethodInvocCount());
						featureVector.setLast_libraryMethodInvocCount(1);
					} else if ((last_featureVector.getLibraryMethodInvocCount()-featureVector.getLibraryMethodInvocCount())< 0){
						//featureVector.setLast_libraryMethodInvocCount(last_featureVector.getLibraryMethodInvocCount()-featureVector.getLibraryMethodInvocCount());
						featureVector.setLast_libraryMethodInvocCount(-1);
					} else {
						//featureVector.setLast_libraryMethodInvocCount(last_featureVector.getLibraryMethodInvocCount()-featureVector.getLibraryMethodInvocCount());
						featureVector.setLast_libraryMethodInvocCount(0);
					}
					
					if ((last_featureVector.getOtherMethodInvocCount()-featureVector.getOtherMethodInvocCount())> 0) {
						//featureVector.setLast_otherMethodInvocCount(last_featureVector.getOtherMethodInvocCount()-featureVector.getOtherMethodInvocCount());
						featureVector.setLast_otherMethodInvocCount(1);
					} else if ((last_featureVector.getOtherMethodInvocCount()-featureVector.getOtherMethodInvocCount())< 0){
						//featureVector.setLast_otherMethodInvocCount(last_featureVector.getOtherMethodInvocCount()-featureVector.getOtherMethodInvocCount());
						featureVector.setLast_otherMethodInvocCount(-1);
					} else {
						//featureVector.setLast_otherMethodInvocCount(last_featureVector.getOtherMethodInvocCount()-featureVector.getOtherMethodInvocCount());
						featureVector.setLast_otherMethodInvocCount(0);
					}
					
					if ((last_featureVector.getUniOPERATORCount()-featureVector.getUniOPERATORCount())> 0) {
						//featureVector.setLast_uniOPERATORCount(last_featureVector.getUniOPERATORCount()-featureVector.getUniOPERATORCount());
						featureVector.setLast_uniOPERATORCount(1);
					} else if ((last_featureVector.getUniOPERATORCount()-featureVector.getUniOPERATORCount())< 0){
						//featureVector.setLast_uniOPERATORCount(last_featureVector.getUniOPERATORCount()-featureVector.getUniOPERATORCount());
						featureVector.setLast_uniOPERATORCount(-1);
					} else {
						//featureVector.setLast_uniOPERATORCount(last_featureVector.getUniOPERATORCount()-featureVector.getUniOPERATORCount());
						featureVector.setLast_uniOPERATORCount(0);
					}
					
					if((last_featureVector.getUniOperandCount()-featureVector.getUniOperandCount())>0){
						//featureVector.setLast_uniOperandCount(last_featureVector.getUniOperandCount()-featureVector.getUniOperandCount());
						featureVector.setLast_uniOperandCount(1);
					} else if ((last_featureVector.getUniOperandCount()-featureVector.getUniOperandCount())<0) {
						//featureVector.setLast_uniOperandCount(last_featureVector.getUniOperandCount()-featureVector.getUniOperandCount());
						featureVector.setLast_uniOperandCount(-1);
					} else {
						//featureVector.setLast_uniOperandCount(last_featureVector.getUniOperandCount()-featureVector.getUniOperandCount());
						featureVector.setLast_uniOperandCount(0);
					}

					if ((last_featureVector.getTotalOPERATORCount()-featureVector.getTotalOPERATORCount())>0) {
						//featureVector.setLast_totalOPERATORCount(last_featureVector.getTotalOPERATORCount()-featureVector.getTotalOPERATORCount());
						featureVector.setLast_totalOPERATORCount(1);
					} else if((last_featureVector.getTotalOPERATORCount()-featureVector.getTotalOPERATORCount())<0) {
						//featureVector.setLast_totalOPERATORCount(last_featureVector.getTotalOPERATORCount()-featureVector.getTotalOPERATORCount());
						featureVector.setLast_totalOPERATORCount(-1);
					} else {
						//featureVector.setLast_totalOPERATORCount(last_featureVector.getTotalOPERATORCount()-featureVector.getTotalOPERATORCount());
						featureVector.setLast_totalOPERATORCount(0);
					}
					
					if((last_featureVector.getTotalOperandCount()-featureVector.getTotalOperandCount())>0){
						//featureVector.setLast_totalOperandCount(last_featureVector.getTotalOperandCount()-featureVector.getTotalOperandCount());
						featureVector.setLast_totalOperandCount(1);
					} else if ((last_featureVector.getTotalOperandCount()-featureVector.getTotalOperandCount())<0) {
						//featureVector.setLast_totalOperandCount(last_featureVector.getTotalOperandCount()-featureVector.getTotalOperandCount());
						featureVector.setLast_totalOperandCount(-1);
					} else {
						//featureVector.setLast_totalOperandCount(last_featureVector.getTotalOperandCount()-featureVector.getTotalOperandCount());
						featureVector.setLast_totalOperandCount(0);
					}
					
					
					for(int l=0 ; l<featureVector.getStruFeature().length ; l++){
						if((last_featureVector.getStruFeature()[l]-featureVector.getStruFeature()[l])>0){
							//last_structuralFeatureP[l] = last_featureVector.getStruFeature()[l]-featureVector.getStruFeature()[l];//����һ��last_structuralFeatureP����   	
							last_structuralFeatureP[l]=1;
						} else if((last_featureVector.getStruFeature()[l]-featureVector.getStruFeature()[l])<0){
							//last_structuralFeatureP[l] = last_featureVector.getStruFeature()[l]-featureVector.getStruFeature()[l];//����һ��last_structuralFeatureP����   	
							last_structuralFeatureP[l]=-1;
						} else {
							//last_structuralFeatureP[l] = last_featureVector.getStruFeature()[l]-featureVector.getStruFeature()[l];//����һ��last_structuralFeatureP����   	
							last_structuralFeatureP[l]=0;
						}				

					}
					
					featureVector.setStructuralFeatureChanges_neighbor(last_structuralFeatureP);
					
					/*
					//labelһ��������
					//label_2����next version
					if(!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
							evo.getCgPattern().contains("CONSISTENTCHANGE")) consist = 1;
					if(evo.getCgPattern().contains("INCONSISTENTCHANGE")) consist = 0;
					*/
					
					//��ȡ�ݻ�����
					//����lastversionofpattern��numberofpatterns
					//��ǰ��һ�汾���ݻ�ģʽ��lastversionofpattern
					//�˰汾֮ǰ������ʷ���ݻ�ģʽ������numberofpatterns
					
					//��ȡ��ǰ��һ�汾���ݻ�ģʽ�ʹ˰汾��֮ǰ������ʷ���ݻ�ģʽ������lastversionofpattern��numberofpatterns
					lastversionofpattern = new int[7];
					numberofpatterns = new int[7];				
				    int flag=1;
				    boolean isSetInitChanges=false;
				    
				    FeatureVector temp_last_featureVector=null ;
					while(evo.getParentID()!=null){

						for(GenealogyEvolution tempEvo : genEvoList){
							if(tempEvo.getID().equals(evo.getParentID())){
								evo = tempEvo;
								break;
							}
						}
						
						if (evo.getCgPattern().contains("STATIC")) {
							numberofpatterns[0] += 1;
							if(flag==1) lastversionofpattern[0]=numberofpatterns[0];
						}
	
						if (evo.getCgPattern().contains("SAME")) {
							numberofpatterns[1] += 1;
							if(flag==1) lastversionofpattern[1]=numberofpatterns[1];
						}
					 
						if (evo.getCgPattern().contains("ADD")) {
							numberofpatterns[2] += 1;
							if(flag==1) lastversionofpattern[2]=numberofpatterns[2];
						}
						
						if (evo.getCgPattern().contains("DELETE")) {
							numberofpatterns[3] += 1;
							if(flag==1) lastversionofpattern[3]=numberofpatterns[3];
						}
						
						if (evo.getCgPattern().contains("SPLIT")) {
							numberofpatterns[4] += 1;
							if(flag==1) lastversionofpattern[4]=numberofpatterns[4];
						}
				

						
						if (!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
								evo.getCgPattern().contains("CONSISTENTCHANGE")) {
							numberofpatterns[5] += 1;
							if(flag==1) lastversionofpattern[5]=numberofpatterns[5];
						}	
						
						if (evo.getCgPattern().contains("INCONSISTENTCHANGE")) {
							numberofpatterns[6] += 1;
							if(flag==1) lastversionofpattern[6]=numberofpatterns[6];
						}
						flag++;	//flag���ڱ�ʾ�Ƿ�����һ�汾	
						
						if(evo.getSrcSize() != -1)	++age;//��¡����
						
						/*
						//��˰汾֮ǰ������ʷ���ݻ�ģʽ������numberofpatterns
						numberofpatterns = new int[7];				
					
						while(evo.getParentID()!=null){
							
							for(GenealogyEvolution tempEvo : genEvoList){
								if(tempEvo.getID().equals(evo.getParentID())){
									evo = tempEvo;
									break;
								}
							}
							
							if (evo.getCgPattern().contains("STATIC"))  numberofpatterns[0] += 1;
							if (evo.getCgPattern().contains("SAME"))	numberofpatterns[1] += 1;
							if (evo.getCgPattern().contains("ADD"))	numberofpatterns[2] += 1;
							if (evo.getCgPattern().contains("DELETE")) numberofpatterns[3] += 1;
							if (evo.getCgPattern().contains("SPLIT")) numberofpatterns[4] += 1;
							
							if (!evo.getCgPattern().contains("INCONSISTENTCHANGE") && 
									evo.getCgPattern().contains("CONSISTENTCHANGE")) numberofpatterns[5] += 1;
							if (evo.getCgPattern().contains("INCONSISTENTCHANGE")) numberofpatterns[6] += 1;
						
							if(evo.getSrcSize() != -1)	++age;//��¡����
						}									
						*/
						
						
						//��ȡ�仯��ʷ�Ķ���
						//�仯��ʷ�����������֣���0��j�ı仯��ʷ��j��j+1�ı仯
						//�仯����ʹ�ô������Լ��㣬��ȡi��i+1�Ĵ������ԣ���i+1�Ĵ������Լ�ȥi�Ĵ�������
						
						//����j��j+1�ı仯��ֱ��ʹ��j+1�Ĵ��������ȥj�Ĵ��������������������
						
						
						//����0��j�ı仯������ʹ��i+1��ȥi�Ĵ���������õ�����d1��d2....
                        //��d��ֳ������ֱ��ۼƼ����ӵ����ı仯�����͸��ı仯����
						//���յõ�����仯���������ĺ͸���,�ֱ����
						
						
						//��ȡ��һ�汾(j-1)�Ĵ������

						last_featureVector = new FeatureVector();
						CloneGroup lastVersionGroup = new CloneGroup();
						if(evo.getSrcVersion() == -1){
							last_init();
							if(featureVector.getStructuralFeatureChanges_fromOriginP()==null){
								featureVector.setStructuralFeatureChanges_fromOriginP(last_structuralFeatureP);
							}
							if(featureVector.getStructuralFeatureChanges_fromOriginN()==null){
								featureVector.setStructuralFeatureChanges_fromOriginN(last_structuralFeatureN);
							}
							break;
						} 
						
						lastVersionGroup = FindCLoneGroup(evo.getSrcVersion(),evo.getSrcCGID()); //��һ�汾��ȡevo.getSrcVersion()	
						ExtractForLastGroup(lastVersionGroup);
						
						if(isSetInitChanges){
							if((temp_last_featureVector.getSourceLine() - last_featureVector.getSourceLine())>0){
								//featureVector.setFromOrigin_souceLinesP(featureVector.getFromOrigin_souceLinesP() + temp_last_featureVector.getSourceLine() - last_featureVector.getSourceLine());
								featureVector.setFromOrigin_souceLinesP(featureVector.getFromOrigin_souceLinesP() + 1);
							}else if((temp_last_featureVector.getSourceLine() - last_featureVector.getSourceLine())<0){
								//featureVector.setFromOrigin_souceLinesN(featureVector.getFromOrigin_souceLinesN() + temp_last_featureVector.getSourceLine() - last_featureVector.getSourceLine());
								featureVector.setFromOrigin_souceLinesN(featureVector.getFromOrigin_souceLinesN() + 1);
							}
							
							if((temp_last_featureVector.getFragCount()-last_featureVector.getFragCount())>0){
								//featureVector.setFromOrigin_fragCountP(featureVector.getFromOrigin_fragCountP() + temp_last_featureVector.getFragCount()-last_featureVector.getFragCount());
								featureVector.setFromOrigin_fragCountP(featureVector.getFromOrigin_fragCountP() + 1);
							} else  if((temp_last_featureVector.getFragCount()-last_featureVector.getFragCount())<0){
								//featureVector.setFromOrigin_fragCountN(featureVector.getFromOrigin_fragCountN() + temp_last_featureVector.getFragCount()-last_featureVector.getFragCount());
								featureVector.setFromOrigin_fragCountN(featureVector.getFromOrigin_fragCountN() + 1);
							}
							
							if ((temp_last_featureVector.getTotalParameterCount()-last_featureVector.getTotalParameterCount())>0) {
								//featureVector.setFromOrigin_totalParameterCountP(featureVector.getFromOrigin_totalParameterCountP() + temp_last_featureVector.getTotalParameterCount()-last_featureVector.getTotalParameterCount());
								featureVector.setFromOrigin_totalParameterCountP(featureVector.getFromOrigin_totalParameterCountP()+ 1);
							} else if ((temp_last_featureVector.getTotalParameterCount()-last_featureVector.getTotalParameterCount())<0){
								//featureVector.setFromOrigin_totalParameterCountN(featureVector.getFromOrigin_totalParameterCountN() + temp_last_featureVector.getTotalParameterCount()-last_featureVector.getTotalParameterCount());
								featureVector.setFromOrigin_totalParameterCountN(featureVector.getFromOrigin_totalParameterCountN() + 1);
							}
							
							if ((temp_last_featureVector.getTotalMethodInvocCount()-last_featureVector.getTotalMethodInvocCount())>0) {
								//featureVector.setFromOrigin_totalMethodInvocCountP(featureVector.getFromOrigin_totalMethodInvocCountP() + temp_last_featureVector.getTotalMethodInvocCount()-last_featureVector.getTotalMethodInvocCount());
								featureVector.setFromOrigin_totalMethodInvocCountP(featureVector.getFromOrigin_totalMethodInvocCountP() + 1);
							} else if ((temp_last_featureVector.getTotalMethodInvocCount()-last_featureVector.getTotalMethodInvocCount())<0){
								//featureVector.setFromOrigin_totalMethodInvocCountN(featureVector.getFromOrigin_totalMethodInvocCountN() + temp_last_featureVector.getTotalMethodInvocCount()-last_featureVector.getTotalMethodInvocCount());
								featureVector.setFromOrigin_totalMethodInvocCountN(featureVector.getFromOrigin_totalMethodInvocCountN() + 1);
							}
							
							if ((temp_last_featureVector.getLocalMethodInvocCount()-last_featureVector.getLocalMethodInvocCount())>0) {
								//featureVector.setFromOrigin_localMethodInvocCountP(featureVector.getFromOrigin_localMethodInvocCountP() + temp_last_featureVector.getLocalMethodInvocCount()-last_featureVector.getLocalMethodInvocCount());
								featureVector.setFromOrigin_localMethodInvocCountP(featureVector.getFromOrigin_localMethodInvocCountP() + 1);
							} else if ((temp_last_featureVector.getLocalMethodInvocCount()-last_featureVector.getLocalMethodInvocCount())<0){
								//featureVector.setFromOrigin_localMethodInvocCountN(featureVector.getFromOrigin_localMethodInvocCountN() + temp_last_featureVector.getLocalMethodInvocCount()-last_featureVector.getLocalMethodInvocCount());
								featureVector.setFromOrigin_localMethodInvocCountN(featureVector.getFromOrigin_localMethodInvocCountN() + 1);
							}
							
							if ((temp_last_featureVector.getLibraryMethodInvocCount()-last_featureVector.getLibraryMethodInvocCount())>0) {
								//featureVector.setFromOrigin_libraryMethodInvocCountP(featureVector.getFromOrigin_libraryMethodInvocCountP() + temp_last_featureVector.getLibraryMethodInvocCount()-last_featureVector.getLibraryMethodInvocCount());
								featureVector.setFromOrigin_libraryMethodInvocCountP(featureVector.getFromOrigin_libraryMethodInvocCountP() + 1);
							} else if ((temp_last_featureVector.getLibraryMethodInvocCount()-last_featureVector.getLibraryMethodInvocCount())<0){
								//featureVector.setFromOrigin_libraryMethodInvocCountN(featureVector.getFromOrigin_libraryMethodInvocCountN() + temp_last_featureVector.getLibraryMethodInvocCount()-last_featureVector.getLibraryMethodInvocCount());
								featureVector.setFromOrigin_libraryMethodInvocCountN(featureVector.getFromOrigin_libraryMethodInvocCountN() + 1);
							}
							
							if ((temp_last_featureVector.getOtherMethodInvocCount() - last_featureVector.getOtherMethodInvocCount())>0) {
								//featureVector.setFromOrigin_otherMethodInvocCountP(featureVector.getFromOrigin_otherMethodInvocCountP() + temp_last_featureVector.getOtherMethodInvocCount() - last_featureVector.getOtherMethodInvocCount());
								featureVector.setFromOrigin_otherMethodInvocCountP(featureVector.getFromOrigin_otherMethodInvocCountP() + 1);
							} else if ((temp_last_featureVector.getOtherMethodInvocCount() - last_featureVector.getOtherMethodInvocCount())<0){
								//featureVector.setFromOrigin_otherMethodInvocCountN(featureVector.getFromOrigin_otherMethodInvocCountN() + temp_last_featureVector.getOtherMethodInvocCount() - last_featureVector.getOtherMethodInvocCount());
								featureVector.setFromOrigin_otherMethodInvocCountN(featureVector.getFromOrigin_otherMethodInvocCountN() +1);
							}
							
							if ((temp_last_featureVector.getUniOPERATORCount()-last_featureVector.getUniOPERATORCount())>0) {
								//featureVector.setFromOrigin_uniOPERATORCountP(featureVector.getFromOrigin_uniOPERATORCountP() + temp_last_featureVector.getUniOPERATORCount()-last_featureVector.getUniOPERATORCount());
								featureVector.setFromOrigin_uniOPERATORCountP(featureVector.getFromOrigin_uniOPERATORCountP() + 1);
							} else if ((temp_last_featureVector.getUniOPERATORCount()-last_featureVector.getUniOPERATORCount())< 0){
								//featureVector.setFromOrigin_uniOPERATORCountN(featureVector.getFromOrigin_uniOPERATORCountN() + temp_last_featureVector.getUniOPERATORCount()-last_featureVector.getUniOPERATORCount());
								featureVector.setFromOrigin_uniOPERATORCountN(featureVector.getFromOrigin_uniOPERATORCountN() + 1);
							}
							
							if ((temp_last_featureVector.getUniOperandCount()-last_featureVector.getUniOperandCount())>0) {
								//featureVector.setFromOrigin_uniOperandCountP(featureVector.getFromOrigin_uniOperandCountP() + temp_last_featureVector.getUniOperandCount()-last_featureVector.getUniOperandCount());
								featureVector.setFromOrigin_uniOperandCountP(featureVector.getFromOrigin_uniOperandCountP() + 1);
							} else if ((temp_last_featureVector.getUniOperandCount()-last_featureVector.getUniOperandCount())<0) {
								//featureVector.setFromOrigin_uniOperandCountN(featureVector.getFromOrigin_uniOperandCountN() + temp_last_featureVector.getUniOperandCount()-last_featureVector.getUniOperandCount());
								featureVector.setFromOrigin_uniOperandCountN(featureVector.getFromOrigin_uniOperandCountN() + 1);
							}
							
							if ((temp_last_featureVector.getTotalOPERATORCount()-last_featureVector.getTotalOPERATORCount())>0) {
								//featureVector.setFromOrigin_totalOPERATORCountP(featureVector.getFromOrigin_totalOPERATORCountP() + temp_last_featureVector.getTotalOPERATORCount()-last_featureVector.getTotalOPERATORCount());
								featureVector.setFromOrigin_totalOPERATORCountP(featureVector.getFromOrigin_totalOPERATORCountP() + 1);
							} else if ((temp_last_featureVector.getTotalOPERATORCount()-last_featureVector.getTotalOPERATORCount())<0){
								//featureVector.setFromOrigin_totalOPERATORCountN(featureVector.getFromOrigin_totalOPERATORCountN() + temp_last_featureVector.getTotalOPERATORCount()-last_featureVector.getTotalOPERATORCount());
								featureVector.setFromOrigin_totalOPERATORCountN(featureVector.getFromOrigin_totalOPERATORCountN() + 1);
							}
							
							if ((temp_last_featureVector.getTotalOperandCount()-last_featureVector.getTotalOperandCount())>0) {
								//featureVector.setFromOrigin_totalOperandCountP(featureVector.getFromOrigin_totalOperandCountP() + temp_last_featureVector.getTotalOperandCount()-last_featureVector.getTotalOperandCount());
								featureVector.setFromOrigin_totalOperandCountP(featureVector.getFromOrigin_totalOperandCountP() + 1);
							} else if ((temp_last_featureVector.getTotalOperandCount()-last_featureVector.getTotalOperandCount())<0){
								//featureVector.setFromOrigin_totalOperandCountN(featureVector.getFromOrigin_totalOperandCountN() + temp_last_featureVector.getTotalOperandCount()-last_featureVector.getTotalOperandCount());
								featureVector.setFromOrigin_totalOperandCountN(featureVector.getFromOrigin_totalOperandCountN() + 1);
							}
							
							for(int l=0 ; l<last_featureVector.getStruFeature().length ; l++){
								if ((temp_last_featureVector.getStruFeature()[l] - last_featureVector.getStruFeature()[l])>0) {
									//last_structuralFeatureP[l] = featureVector.getStructuralFeatureChanges_fromOriginP()[l] + temp_last_featureVector.getStruFeature()[l] - last_featureVector.getStruFeature()[l]; 
									last_structuralFeatureP[l] = featureVector.getStructuralFeatureChanges_fromOriginP()[l] + 1;
								} else if ((temp_last_featureVector.getStruFeature()[l] - last_featureVector.getStruFeature()[l])<0){
									//last_structuralFeatureN[l] = featureVector.getStructuralFeatureChanges_fromOriginN()[l] + temp_last_featureVector.getStruFeature()[l] - last_featureVector.getStruFeature()[l];
									last_structuralFeatureN[l] = featureVector.getStructuralFeatureChanges_fromOriginN()[l] + 1;
								} 										
							}
							
							featureVector.setStructuralFeatureChanges_fromOriginP(last_structuralFeatureP);							
							featureVector.setStructuralFeatureChanges_fromOriginN(last_structuralFeatureN);
						}
						if(!isSetInitChanges){//�����������һ�汾�����ڵı仯
							if(featureVector.getSourceLine()-last_featureVector.getSourceLine()>0){
								//featureVector.setFromOrigin_souceLinesP(featureVector.getSourceLine()-last_featureVector.getSourceLine());
								featureVector.setFromOrigin_souceLinesP(1);
							} else if(featureVector.getSourceLine()-last_featureVector.getSourceLine()<0){
								//featureVector.setFromOrigin_souceLinesN(featureVector.getSourceLine()-last_featureVector.getSourceLine());
								featureVector.setFromOrigin_souceLinesN(1);
							}

							if(featureVector.getFragCount()-last_featureVector.getFragCount()>0){
								//featureVector.setFromOrigin_fragCountP(featureVector.getFragCount()-last_featureVector.getFragCount());
								featureVector.setFromOrigin_fragCountP(1);
							} else if(featureVector.getFragCount()-last_featureVector.getFragCount()<0){
								//featureVector.setFromOrigin_fragCountN(featureVector.getFragCount()-last_featureVector.getFragCount());
								featureVector.setFromOrigin_fragCountN(1);
							}
							
							if(featureVector.getTotalParameterCount()-last_featureVector.getTotalParameterCount()>0){
								//featureVector.setFromOrigin_totalParameterCountP(featureVector.getTotalParameterCount()-last_featureVector.getTotalParameterCount());
								featureVector.setFromOrigin_totalParameterCountP(1);
							} else if(featureVector.getTotalParameterCount()-last_featureVector.getTotalParameterCount()<0){
								//featureVector.setFromOrigin_totalParameterCountN(featureVector.getTotalParameterCount()-last_featureVector.getTotalParameterCount());
								featureVector.setFromOrigin_totalParameterCountN(1);
							}
							
							if(featureVector.getTotalMethodInvocCount()-last_featureVector.getTotalMethodInvocCount()>0){
								//featureVector.setFromOrigin_totalMethodInvocCountP(featureVector.getTotalMethodInvocCount()-last_featureVector.getTotalMethodInvocCount());
								featureVector.setFromOrigin_totalMethodInvocCountP(1);
							} else if(featureVector.getTotalMethodInvocCount()-last_featureVector.getTotalMethodInvocCount()<0){
								//featureVector.setFromOrigin_totalMethodInvocCountN(featureVector.getTotalMethodInvocCount()-last_featureVector.getTotalMethodInvocCount());
								featureVector.setFromOrigin_totalMethodInvocCountN(1);
							}
							
							if(featureVector.getLocalMethodInvocCount()-last_featureVector.getLocalMethodInvocCount()>0){
								//featureVector.setFromOrigin_localMethodInvocCountP(featureVector.getLocalMethodInvocCount()-last_featureVector.getLocalMethodInvocCount());
								featureVector.setFromOrigin_localMethodInvocCountP(1);
							} else if(featureVector.getLocalMethodInvocCount()-last_featureVector.getLocalMethodInvocCount()<0){
								//featureVector.setFromOrigin_localMethodInvocCountN(featureVector.getLocalMethodInvocCount()-last_featureVector.getLocalMethodInvocCount());
								featureVector.setFromOrigin_localMethodInvocCountN(1);
							}

							if(featureVector.getLibraryMethodInvocCount()-last_featureVector.getLibraryMethodInvocCount()>0){
								//featureVector.setFromOrigin_libraryMethodInvocCountP(featureVector.getLibraryMethodInvocCount()-last_featureVector.getLibraryMethodInvocCount());
								featureVector.setFromOrigin_libraryMethodInvocCountP(1);
							} else if(featureVector.getLibraryMethodInvocCount()-last_featureVector.getLibraryMethodInvocCount()<0){
								//featureVector.setFromOrigin_libraryMethodInvocCountN(featureVector.getLibraryMethodInvocCount()-last_featureVector.getLibraryMethodInvocCount());
								featureVector.setFromOrigin_libraryMethodInvocCountN(1);
							}
							
							if(featureVector.getOtherMethodInvocCount() - last_featureVector.getOtherMethodInvocCount()>0){
								//featureVector.setFromOrigin_otherMethodInvocCountP(featureVector.getOtherMethodInvocCount() - last_featureVector.getOtherMethodInvocCount());
								featureVector.setFromOrigin_otherMethodInvocCountP(1);
							} else if(featureVector.getOtherMethodInvocCount() - last_featureVector.getOtherMethodInvocCount()<0){
								//featureVector.setFromOrigin_otherMethodInvocCountN(featureVector.getOtherMethodInvocCount() - last_featureVector.getOtherMethodInvocCount());
								featureVector.setFromOrigin_otherMethodInvocCountN(1);
							}
							
							if(featureVector.getUniOPERATORCount()-last_featureVector.getUniOPERATORCount()>0){
								//featureVector.setFromOrigin_uniOPERATORCountP(featureVector.getUniOPERATORCount()-last_featureVector.getUniOPERATORCount());
								featureVector.setFromOrigin_uniOPERATORCountP(1);
							} else 	if(featureVector.getUniOPERATORCount()-last_featureVector.getUniOPERATORCount()<0){
								//featureVector.setFromOrigin_uniOPERATORCountN(featureVector.getUniOPERATORCount()-last_featureVector.getUniOPERATORCount());
								featureVector.setFromOrigin_uniOPERATORCountN(1);
							}
							
							if(featureVector.getUniOperandCount()-last_featureVector.getUniOperandCount()>0){
								//featureVector.setFromOrigin_uniOperandCountP(featureVector.getUniOperandCount()-last_featureVector.getUniOperandCount());
								featureVector.setFromOrigin_uniOperandCountP(1);
							} else if(featureVector.getUniOperandCount()-last_featureVector.getUniOperandCount()<0){
								//featureVector.setFromOrigin_uniOperandCountN(featureVector.getUniOperandCount()-last_featureVector.getUniOperandCount());
								featureVector.setFromOrigin_uniOperandCountN(1);
							}
							
							if(featureVector.getTotalOPERATORCount()-last_featureVector.getTotalOPERATORCount()>0){
								//featureVector.setFromOrigin_totalOPERATORCountP(featureVector.getTotalOPERATORCount()-last_featureVector.getTotalOPERATORCount());
								featureVector.setFromOrigin_totalOPERATORCountP(1);
							} else if(featureVector.getTotalOPERATORCount()-last_featureVector.getTotalOPERATORCount()<0){
								//featureVector.setFromOrigin_totalOPERATORCountN(featureVector.getTotalOPERATORCount()-last_featureVector.getTotalOPERATORCount());
								featureVector.setFromOrigin_totalOPERATORCountN(1);
							}
							
							if(featureVector.getTotalOperandCount()-last_featureVector.getTotalOperandCount()>0){
								//featureVector.setFromOrigin_totalOperandCountP(featureVector.getTotalOperandCount()-last_featureVector.getTotalOperandCount());
								featureVector.setFromOrigin_totalOperandCountP(1);
							} else if(featureVector.getTotalOperandCount()-last_featureVector.getTotalOperandCount()<0){
								//featureVector.setFromOrigin_totalOperandCountN(featureVector.getTotalOperandCount()-last_featureVector.getTotalOperandCount());
								featureVector.setFromOrigin_totalOperandCountN(1);
							}
							
							for(int l=0 ; l<featureVector.getStruFeature().length ; l++){
								if(featureVector.getStruFeature()[l] - last_featureVector.getStruFeature()[l]>0){
									//last_structuralFeatureP[l] = featureVector.getStruFeature()[l] - last_featureVector.getStruFeature()[l];
									last_structuralFeatureP[l] = 1;
								} else if(featureVector.getStruFeature()[l] - last_featureVector.getStruFeature()[l]<0){
									//last_structuralFeatureN[l] = featureVector.getStruFeature()[l] - last_featureVector.getStruFeature()[l];
									last_structuralFeatureN[l]=1;
								}
							}
							featureVector.setStructuralFeatureChanges_fromOriginP(last_structuralFeatureP);
							featureVector.setStructuralFeatureChanges_fromOriginN(last_structuralFeatureN);
							
							isSetInitChanges = true;//���ñ�־��
						}
						
						temp_last_featureVector = new FeatureVector();
						temp_last_featureVector = last_featureVector;
					}
					
					last_init();
					if(featureVector.getStructuralFeatureChanges_fromOriginP()==null){
						featureVector.setStructuralFeatureChanges_fromOriginP(last_structuralFeatureP);
					}
					if(featureVector.getStructuralFeatureChanges_fromOriginN()==null){
						featureVector.setStructuralFeatureChanges_fromOriginN(last_structuralFeatureN);
					}
					

								
					//�����������������
					
					
					this.featureVector.setConsistence(consist);
					this.featureVector.setAge(age);
					this.featureVector.setEvoPattern(numberofpatterns);
					this.featureVector.setlastevoPattern(lastversionofpattern);
					
					VariationInformation.featureVectorList.add(featureVector);
				}
			}
		}
		
		WekaOperations.WriteFeaturesToArff(Path._clonesFolderPath + "FeatureVector_2.arff",2);
		
	}//ExtractFeature()
	
	private void ExtractForLastGroup(CloneGroup group){
		last_init();
		int length = group.getClonefragment().size();
		this.last_featureVector.setFragCount(length);
		for(int i=0;i<length;i++){
			//����ͽṹ����
			CloneFragment frag = group.getClonefragment().get(i);
			last_souceLines += (frag.getEndLine() - frag.getStartLine()+1);
			
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
		
	  		last_totalParaCount += invocFeatureVisitor.getTotalParameterCount();
	  		last_totalMethodInvocCount += invocFeatureVisitor.getTotalMethodInvocCount();
	  		last_localMethodInvocCount += invocFeatureVisitor.getLocalMethodInvocCount();
	  		last_libraryMethodInvocCount += invocFeatureVisitor.getLibraryMethodInvocCount();
	  		last_otherMethodInvocCount += invocFeatureVisitor.getOtherMethodInvocCount();
	  		
	  		//halstead����  
	        cloneCode = PreProcess.clearComment(cloneCode);
	        cloneCode = PreProcess.clearString(cloneCode);
	        cloneCode = PreProcess.clearImport(cloneCode);
	        HalsteadMetric halMetric = new HalsteadMetric(cloneCode);
	        
	        last_uniOPERATORCount += halMetric.getUniOPERATORCount();
	        last_uniOperandCount += halMetric.getUniOperandCount();
	        last_totalOPERATORCount += halMetric.getTotalOPERATORCount(); 
	        last_totalOperandCount += halMetric.getTotalOperandCount();
	        
	        //�ṹ����
	        StructuralFeatureVisitor strucFeatureVisitor = new StructuralFeatureVisitor(startPos,endPos);
	        cu.accept(strucFeatureVisitor);
	        for(int l=0 ; l<strucFeatureVisitor.getStructuralFeature().length ; l++)
	        	last_structuralFeatureP[l] += strucFeatureVisitor.getStructuralFeature()[l];  //����һ��last_structuralFeatureP��������ʵ������
		}
		
		this.last_featureVector.setSourceLine(last_souceLines/length);
		this.last_featureVector.setTotalParameterCount(last_totalParaCount/length);
  		this.last_featureVector.setTotalMethodInvocCount(last_totalMethodInvocCount/length);
  		this.last_featureVector.setLocalMethodInvocCount(last_localMethodInvocCount/length);
  		this.last_featureVector.setLibraryMethodInvocCount(last_libraryMethodInvocCount/length);
  		this.last_featureVector.setOtherMethodInvocCount(last_otherMethodInvocCount/length);

        this.last_featureVector.setUniOPERATORCount(last_uniOPERATORCount/length);
        this.last_featureVector.setUniOperandCount(last_uniOperandCount/length);
        this.last_featureVector.setTotalOPERATORCount(last_totalOPERATORCount/length);
        this.last_featureVector.setTotalOperandCount(last_totalOperandCount/length);  
        for(int k=0;k<this.last_structuralFeatureP.length;k++){
        	last_structuralFeatureP[k] = last_structuralFeatureP[k]/length;
        }
        this.last_featureVector.setStruFeature(last_structuralFeatureP);
	}
	
	
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
