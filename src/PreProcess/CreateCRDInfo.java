package PreProcess;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import CRDInformation.BlockInfo;
import CRDInformation.CloneSourceInfo;
import CRDInformation.MethodInfo;
import CloneRepresentation.CRD;
import CloneRepresentation.CloneFragment;
import CloneRepresentation.CloneGroup;
import Global.Path;
import Global.VariationInformation;

public class CreateCRDInfo {
	private CRD crd;
	
	//�ı����ƶ���ֵĬ��ֵ0.8
    public static float defaultTextSimTh = (float)0.7;  //�˴�0.7����Դ��nicad������ƿ�¡����ֵΪ0.7
    //λ�ø�������ֵ
    public static float locationOverlap1 = (float)0.5;  //��METHODINFOMATCH�����ʹ��
    public static float locationOverlap2 = (float)0.7;  //��METHODNAMEMATCH�����ʹ��
	
    //��������CRDƥ��̶ȵ�ö������
    public enum CRDMatchLevel{
        //�ı�ƥ�����˼�����ƶȸ�����ֵ��Diff��Ĭ��ֵΪ0.8��
        DIFFERENT,    //��ȫ��ͬ�����㼶��Ϣ��ͬ���ı���ƥ�䣩
        FILECLASSMATCH,     //�ļ���������Ϣ����ͬ���޷����򷽷���Ϣ��ͬ���ı�ƥ��
        METHODNAMEMATCH,    //�ļ�������������������ͬ��������ͬ������Ϣ��ͬ���ı�ƥ��
        METHODINFOMATCH,    //�ļ���������������������������ͬ������Ϣ��ͬ���ı�ƥ��
        BLOCKMATCH;     //�ļ�����������������Ϣ������Ϣ��ͬ���ı�ƥ��
    }
    
	public void CreateCRDForSys(){

		for(CloneGroup group : VariationInformation.cloneGroup){  //�޸�VariationInformation.cloneGroup���ݽṹ
			for(CloneFragment fragment : group.getClonefragment()){
				crd = new CRD();
				String subSysPath = Path._subSysDirectory + "\\" + fragment.getPath();
				
				BufferedInputStream bufferedInputStream;
			    byte[] input = null;
				try {
					bufferedInputStream = new BufferedInputStream(new FileInputStream(subSysPath));
					input = new byte[bufferedInputStream.available()];  
			        bufferedInputStream.read(input);  
			        bufferedInputStream.close(); 
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {  
		            e.printStackTrace();  
		        }     
				ASTParser astParser = ASTParser.newParser(AST.JLS8);
				astParser.setSource(new String(input).toCharArray());
				astParser.setKind(ASTParser.K_COMPILATION_UNIT);
				CompilationUnit cu = (CompilationUnit) (astParser.createAST(null));
				int startPos = cu.getPosition(fragment.getStartLine(), 0);
				int endPos = cu.getPosition(fragment.getEndLine() + 1, 0); //endLine + 1 or startPos + 1
				ExtractCRDVisitor extraCrdVisitor = new ExtractCRDVisitor(startPos,endPos);
				cu.accept(extraCrdVisitor);
				
				//���CRD��Ϣ
				crd.setFileName(fragment.getPath());
				crd.setClassName(extraCrdVisitor.className);
				crd.setMethodInfo(extraCrdVisitor.methodInfo);
				crd.setBlockInfos(extraCrdVisitor.blockInfos);
				int start = fragment.getStartLine();
				int end = fragment.getEndLine();
				crd.setStartLine(String.valueOf(start));//���Ե�startline
				crd.setEndLine(String.valueOf(end));
				if(extraCrdVisitor.relStartLine != null){
					crd.setRelStartLine(extraCrdVisitor.relStartLine);
					crd.setRelEndLine(String.valueOf(end - start + Integer.parseInt(crd.getRelStartLine())));
				}
				else if(extraCrdVisitor.pOffset != -1){
					crd.setRelStartLine(String.valueOf(fragment.getStartLine() - cu.getLineNumber(extraCrdVisitor.pOffset) + 1));//��ʼΪ1
					crd.setRelEndLine(String.valueOf(end - start + Integer.parseInt(crd.getRelStartLine())));
				}
				else 
					crd.setRelStartLine(null);

				//��CRD���뵽fragment������
				fragment.setCRD(crd);
			}
		}
	
/*		int index = 0;
		for(ClonesInVersion clones : VariationInformation.cloneInVersion){ //�޸�VariationInformation.cloneInVersion���ݽṹ
			List<CloneGroup> tempCloneGroup;
			for(CloneGroup group : VariationInformation.cloneGroup){
				if(group.getVersionID() == clones.getVersionID()){
					
				}
			}
		}*/
		
	}
	
	public static CRDMatchLevel GetCRDMatchLevel(CRD src,CRD dest) {
		// ��ȥ�ļ����ַ�����ϵͳ���ƵĲ���
		String srcFileName = src.getFileName().substring(src.getFileName().indexOf("/")+1);
		String destFileName = dest.getFileName().substring(dest.getFileName().indexOf("/")+1);
		
		// �����ض����룬�����·�֧�У�ֻ��һ��·��
		if (!srcFileName.equals(destFileName)) {
			return CRDMatchLevel.DIFFERENT;
		} // �ļ�����ͬ����Ϊ��ƥ��
		else{// �ļ���Ϣ��ͬ
			if ((src.getClassName() != null && dest.getClassName() != null)&& !src.getClassName().equals(dest.getClassName())
					|| src.getClassName() != null && dest.getClassName() == null || src.getClassName() == null && dest.getClassName() != null) {
				return CRDMatchLevel.DIFFERENT;
			} // ����Ϣ��ͬ����Ϊ��ƥ��
			else{// ����Ϣ��ͬ������������Ϣ��Ϊ��
				// ������߷�����Ϣ��Ϊ��
				if (src.getMethodInfo() == null && dest.getMethodInfo() == null) {
					return CRDMatchLevel.FILECLASSMATCH;
				}
				// ��������һ��������ϢΪ�գ���һ����Ϊ��
				else if (src.getMethodInfo() != null && dest.getMethodInfo() == null || src.getMethodInfo() == null && dest.getMethodInfo() != null) {
					return CRDMatchLevel.DIFFERENT;
				}
				// ������߷�����Ϣ����Ϊ��
				else {		
					// ���������Ϣ��ͬ����������������Ϣ����ͬ����������Ϣ
					if (src.getMethodInfo().equals(dest.getMethodInfo())) {
						if (src.getBlockInfos() != null && dest.getBlockInfos() != null) {
							if(compareWithBlockList(src.getBlockInfos(),dest.getBlockInfos()))// ����Ϣ��ͬ	
								return CRDMatchLevel.BLOCKMATCH;
							else// ����Ϣ��ͬ
								return CRDMatchLevel.METHODINFOMATCH;
						}
						// ��һ������ϢΪ�գ���һ����Ϊ�գ�����������Ϣ��Ϊ�գ�����Granularity=functions�������
						else 
							return CRDMatchLevel.METHODINFOMATCH;
					}
					// ��������ͬ��������Ϣ��ͬ
					else if ((src.getMethodInfo().mName == dest.getMethodInfo().mName)
							&& !MethodInfo.compare(src.getMethodInfo().mParaTypeList, dest.getMethodInfo().mParaTypeList)) {
						return CRDMatchLevel.METHODNAMEMATCH;
					}
					// ��������������Ϣ����ͬ
					else {
						return CRDMatchLevel.FILECLASSMATCH;
					}
				}
			}
		}
	}
	
	public static boolean compareWithBlockList(List<BlockInfo> a, List<BlockInfo> b) {
	    if(a.size() != b.size())
	        return false;

	    for(int i=0;i<a.size();i++){
	    	for(int j=0;j<b.size();j++)
	        if(!a.get(i).equals(b.get(i)))
	            return false;
	    }
	    return true;
	}
	
    // �������ο�¡�����λ�ø����ʣ������ڷ����е����λ�ü��㣩
    public static float GetLocationOverlap(CRD srcCrd, CRD destCrd){
        if (srcCrd.getRelStartLine() == null || srcCrd.getRelEndLine() == null || destCrd.getRelStartLine() == null || destCrd.getRelEndLine() == null)	
        	return -1;
        
        int startLine1, startLine2, endLine1, endLine2;
        startLine1 = Integer.parseInt(srcCrd.getRelStartLine());
        endLine1 = Integer.parseInt(srcCrd.getRelEndLine());
        startLine2 = Integer.parseInt(destCrd.getRelStartLine());
        endLine2 = Integer.parseInt(destCrd.getRelEndLine());
        int startLine = startLine1 > startLine2 ? startLine1 : startLine2;  //ȡstartLine�нϴ��
        int endLine = endLine1 < endLine2 ? endLine1 : endLine2;    //ȡendLine�н�С��
        //����overLapping
        return (float)(endLine - startLine) / (float)(endLine2 - startLine2);
    }
	
    public static float GetTextSimilarity(CRD srcCrd, CRD destCrd, boolean ignoreEmptyLines) {
	    	List<String> srcFileContent = new ArrayList<String>();
	        List<String> destFileContent = new ArrayList<String>();
	        List<String> srcFragment = new ArrayList<String>();
	        List<String> destFragment = new ArrayList<String>();
	        CloneSourceInfo info = new CloneSourceInfo();
	        
	        // ���srcCrdNodeԴ�����ı�
	        info.sourcePath = srcCrd.getFileName();  //���Դ�ļ���
	        String fullName = Path._subSysDirectory + "\\" + info.sourcePath;
	        //��ȡԴ����������
	        info.startLine =Integer.parseInt(srcCrd.getStartLine());
	        info.endLine = Integer.parseInt(srcCrd.getEndLine());
	        
	        srcFileContent = GetFileContent(fullName);
	        srcFragment = GetCFSourceFromSourcInfo(srcFileContent, info);

	       	// ���destCrdNodeԴ�����ı�
	        info.sourcePath = destCrd.getFileName();    //���Դ�ļ���
	        fullName = Path._subSysDirectory + "\\" + info.sourcePath;
	        //��ȡԴ����������
	        info.startLine =Integer.parseInt(destCrd.getStartLine());
	        info.endLine = Integer.parseInt(destCrd.getEndLine());
	     
	        destFileContent = GetFileContent(fullName);
	        destFragment = GetCFSourceFromSourcInfo(destFileContent, info);

	        //ʹ��Diff��������δ�������ƶ�
	        Diff.UseDefaultStrSimTh();  //ʹ�������ƶ���ֵĬ��ֵ0.5
	        Diff.DiffInfo diffFile = new Diff().DiffFiles(srcFragment, destFragment);
	        float sim = Diff.FileSimilarity(diffFile, srcFragment.size(), destFragment.size(), ignoreEmptyLines);

	        return sim;
	}
	 
	public static List<String> GetFileContent(String fileName){  
		List<String> content = new ArrayList<String>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(fileName)));
			String str = reader.readLine();
			while (str != null){
				content.add(str);
	            str = reader.readLine();
	        }
	        reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

    public static List<String> GetCFSourceFromSourcInfo(List<String> source, CloneSourceInfo sourceInfo){
        List<String> codeClone = new ArrayList<String>();
        for (int i = sourceInfo.startLine - 1; i < sourceInfo.endLine; i++) { //ע�⣬������0���㣬���кŴ�1����
        	codeClone.add(source.get(i).toString()); 
        }
        return codeClone;
    }
    public static List<String> GetCFSourceFromCRDInfo(List<String> source,int start,int end){
        List<String> codeClone = new ArrayList<String>();
        for (int i = start - 1; i < end; i++) { //ע�⣬������0���㣬���кŴ�1����
        	codeClone.add(source.get(i).toString()); 
        }
        return codeClone;
    }

}
