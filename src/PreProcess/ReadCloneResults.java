package PreProcess;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import CloneRepresentation.CloneFragment;
import CloneRepresentation.CloneGroup;
import CloneRepresentation.ClonesInVersion;

public class ReadCloneResults {

	private static Document cloneResult;
	public ClonesInVersion inVersion = new ClonesInVersion();
	public List<CloneGroup> groupList = new ArrayList<CloneGroup>();
	
	private static int versionID;
	
	public ReadCloneResults(String fileName,int Nicad) {
		cloneResult = XML.Load(fileName);
		
		List<CloneFragment> fragmentList;
		int fragmentTotal=0;
		
		String path = XML.GetAttriValue(cloneResult, "sourceFile", 0, "path");
		String SystemVersion = path.substring(0, path.indexOf("/"));
		inVersion.setSystemVersion(SystemVersion);
		inVersion.setVersionID(versionID);
		int nclasses = cloneResult.getElementsByTagName("cloneClass").getLength();
		inVersion.setNumberofCG(nclasses);
		
		CloneGroup group;
		CloneFragment fragment;
		for(int i=0;i<nclasses;i++){//每个class
			int nclones = (cloneResult.getElementsByTagName("cloneClass").item(i).getChildNodes().getLength())/2;//避开会车
			fragmentTotal += nclones;
			group = new CloneGroup();
			group.setCGID(i+1);
			group.setVersionID(versionID);
			group.setNumberofCF(nclones);
			//group.setSimilarity(80);
			
			fragmentList = new ArrayList<CloneFragment>();
			for(int j=0;j<group.getNumberofCF();j++){//每个节点下的clone
				fragment = new CloneFragment();
				int sourceFileId = Integer.parseInt(XML.GetChildAttriValue(cloneResult, "cloneClass", i, j, "sourceFileId"));
				String file = XML.GetAttriValue(cloneResult, "sourceFile", sourceFileId, "path");
				fragment.setCFID(j+1);
				fragment.setCGID(group.getCGID());
				fragment.setVersionID(versionID);
				fragment.setPath(file);
				fragment.setFileName(file.substring(file.indexOf("/"), file.length()));
				fragment.setStartLine(Integer.parseInt(XML.GetChildAttriValue(cloneResult, "cloneClass", i, j, "startLine")));
				fragment.setEndLine(Integer.parseInt(XML.GetChildAttriValue(cloneResult, "cloneClass", i, j, "endLine")));
				fragmentList.add(fragment);
			}	
			group.setClonefragment(fragmentList);
			groupList.add(group);
		}
		
		inVersion.setNumberofCF(fragmentTotal);
		inVersion.setCloneGroup(groupList);
		
		++versionID;
	}
}
