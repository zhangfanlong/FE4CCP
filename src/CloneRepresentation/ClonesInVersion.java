package CloneRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZFL
 *ĳһ���汾�Ŀ�¡����
 *1.�ļ���ȡ����
 *2.������ȡ
 *3.
 */
public class ClonesInVersion {

	private int NumberofCG;//��¡�����
	private int NumberofCF;//��¡Ƭ�θ���
	private int VersionID;//�汾��
	private String SystemVersion;//ϵͳ�汾��
	private List<CloneGroup> cloneGroup;
	
	
	public void setNumberofCG(int NumberofCG){
		this.NumberofCG=NumberofCG;
	}
	
	public int getNumberofCG(){
		return NumberofCG;
	}
	
	public void setNumberofCF(int NumberofCF){
		this.NumberofCF=NumberofCF;
	}
	
	public int getNumberofCF(){
		return NumberofCF;
	}
	
	public void setVersionID(int VersionID){
		this.VersionID=VersionID;
	}
	
	public int getVersionID(){
		return VersionID;
	}
	
	public void setSystemVersion(String SystemVersion){
		this.SystemVersion=SystemVersion;
	}
	
	public String getSystemVersion(){
		return SystemVersion;
	}
	
	public List<CloneGroup> getCloneGroup() {
		return cloneGroup;
	}

	public void setCloneGroup(List<CloneGroup> cloneGroup) {
		this.cloneGroup = new ArrayList<CloneGroup>();
		this.cloneGroup = cloneGroup;
	}

	
	
	
}
