package CloneRepresentation;

import java.util.HashMap;
//import java.util.TreeMap;


/**
 * @author ZFL
 *ʵ�ְ汾�Ͱ汾�ŵĶ�Ӧ
 *1.�汾����ϵͳ�汾�ŵĶ�Ӧ��0��1��2��3��Ӧϵͳ�İ汾�ţ�
 *2.�汾�Ų�ѯ
 *3.ϵͳ�汾�Ų�ѯ
 *
 */

public class VersionMappingSystem {
	
	//�������ݽṹ
	HashMap<Integer, String> NummapVersion; //���ݽṹ:�汾����--ϵͳ�汾��
	//Integer StartVersion;
	//Integer EndVersion;
	Integer VersionNumer;
	
	//��ʼ��
	public VersionMappingSystem() {
		// TODO Auto-generated constructor stub
		 NummapVersion = new HashMap<>();//��������
		 //���Ԫ��
		 NummapVersion.put(0, "V1");	 
		 
	}
	
	//��ѯϵͳ�汾��
		String LookupSystemVersion(Integer Version){
			
			return NummapVersion.get(Version);
		}	
	
	//��ѯ�汾��
	//Integer lookupVersion(String SystemVersion){
		
	//	return NummapVersion.get(SystemVersion);
	//}
	
		//�ܰ汾��
		public void setVersionNumer() {
			VersionNumer = NummapVersion.size();
		}
	
		public Integer getVersionNumer() {
			this.setVersionNumer();
			return VersionNumer;
		}
}
