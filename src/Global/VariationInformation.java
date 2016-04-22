package Global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CloneRepresentation.CloneGenealogy;
import CloneRepresentation.CloneGroup;
import CloneRepresentation.ClonesInVersion;
import CloneRepresentation.GroupMapping;
import ExtractFeatures.FeatureVector;


public class VariationInformation {
	
	public static Map<String,List<String>> allVersionJavaFiles;//ϵͳ��������<"dnsjava-1.2.0",List>
	public static List<ClonesInVersion> clonesInVersion;
	public static List<CloneGroup> cloneGroup;
	
	public static List<GroupMapping> mappingInfo; //�Կ�¡ȺΪ��С��λ�洢map 
	public static List<GroupMapping> unMappedSrcInfo;//ûӳ���ϵ� ֻ��Src��   �汾��,��¡ȺID,��¡Ⱥ��С
	public static List<GroupMapping> unMappedDestInfo;//ֻ��Dest��   �汾��,��¡ȺID,��¡Ⱥ��С
	
	public static List<CloneGenealogy> cloneGenealogy;//����ϵͳ�Ŀ�¡ֱϵ
	public static List<CloneGenealogy> singleCgGenealogyList;  //����¡Ⱥ��ϵ�б�       //ֻ�п�ʼ�汾�������ڰ汾�������汾��¡ȺID
	
	public static List<FeatureVector> featureVectorList;
	
	public static void init (){	//Ϊȫ�ֱ�����ʼ��		
		VariationInformation.allVersionJavaFiles = new HashMap<String,List<String>>();
		VariationInformation.clonesInVersion = new ArrayList<ClonesInVersion>();
		VariationInformation.cloneGroup = new ArrayList<CloneGroup>();
		VariationInformation.mappingInfo = new ArrayList<GroupMapping>();
		VariationInformation.unMappedDestInfo = new ArrayList<GroupMapping>();
		VariationInformation.unMappedSrcInfo = new ArrayList<GroupMapping>();
		VariationInformation.cloneGenealogy = new ArrayList<CloneGenealogy>();
		VariationInformation.singleCgGenealogyList = new ArrayList<CloneGenealogy>(); 
		//VariationInformation.featureVectorList = new ArrayList<FeatureVector>();
		
	}
	
}
