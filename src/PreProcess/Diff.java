package PreProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LCSItem{		//LCS����Ľṹ
    public boolean isExactSame;    //��Ǵ����Ƿ�����ȫ��ͬ�������޸�
    public String lineContent; //��ͬ���е�����
    public int lineOfFileA;    //������FileA�е��кţ�������+1��
    public int lintOfFileB;    //������FileB�е��кţ�������+1��
}

class ListStringLCS{   // ArrayListLCS�࣬�������ArrayList��LCS�ļ���

    public List<String> strA;
    public List<String> strB;
    public int length;
    //pointerArray�Ǵ�ű�ǵĶ�ά���飬U��ʾ���ϣ�L��ʾ����Y��ʾ�����ϣ�\0��ʾ�գ�δ��ֵ��
    private char[][] pointerArray;
    //LengthArray�Ǵ��lcs���ȵĶ�ά����
    private int[][] lengthArray;
    public ArrayList<LCSItem> lcs;

    public ListStringLCS(List<String> a, List<String> b){
    
        strA = new ArrayList<String>(a);
        strB = new ArrayList<String>(b);
    }

    // ���strA��strB��LCS��������lcs��Ա�У�������LCS������length��Ա��
    public void GetLCS() {
        this.length = LCSLength(this.strA, this.strB);
        this.lcs = new ArrayList<LCSItem>();
        PrintLCS(strA.size(), strB.size(), this.length);
    }


    // ��ӡ����¼��LCS������õݹ鷽����ʹ��LCS��MarkArray��Ա������lcs��Ա
    // <param name="indexLCS">��������ṩ��ǰ��ӡ��LCS���������</param>
    private void PrintLCS(int indexA, int indexB, int indexLCS){

        if (indexA == 0 || indexB == 0)	return; 
        if (this.pointerArray[indexA][indexB] == 'Y'){
        
        	PrintLCS(indexA - 1, indexB - 1, indexLCS - 1);
            LCSItem newItem = new LCSItem();
            //�������ȫ��ͬ�����������һ������
            if ((strA.get(indexA - 1)).equals(strB.get(indexB - 1))){
                newItem.isExactSame = true;
                newItem.lineContent = strA.get(indexA - 1);
            }
            else{	//�޸ĵ�����������ӵ�һ�𣬴��롣��$DIVIDER$���ָ�
                newItem.isExactSame = false;
                newItem.lineContent = strA.get(indexA - 1) + "$DIVIDER$" + strB.get(indexB - 1);
            }
            newItem.lineOfFileA = indexA;   //indexҲ�Ǵ�1��ʼ����˲���Ҫ+1
            newItem.lintOfFileB = indexB;
            //this.lcs[indexLCS] = newItem;
            this.lcs.add(newItem);
        }
        else if (this.pointerArray[indexA][indexB] == 'U'){
        	PrintLCS(indexA - 1, indexB, indexLCS); 
        }
        else{
         PrintLCS(indexA, indexB - 1, indexLCS); 
        }
    }

    public static String TrimChars(String s){// �����ַ����ж���Ŀո�����޸Ļس�(\r\n)Ϊ����(\n)����ɾ�������\t
        
    	//ע��Unix ϵͳ�ÿ�н�βֻ�С�<����>��������\n����Windowsϵͳ���棬ÿ�н�β�ǡ�<�س�><����>�������� \r\n��
        s = s.replace("\r", "");    //�޸Ļس�(\r\n)Ϊ����(\n)
        
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(s);
        s = matcher.replaceAll(" ");//ɾ������Ŀո�
 
        pattern = Pattern.compile("\\t+");
        matcher = pattern.matcher(s);
        s = matcher.replaceAll(" ");  //�滻һ�������Ʊ��Ϊ�ո�

        s.trim();   //ɾ����ͷ�ͽ�β�Ŀո�
        return s;
    }
    
  
    // ���������ַ��������ƶȡ���ȫ��ͬʱΪ1����ȫ��ͬ��LCS����Ϊ0��ʱΪ0
    public static float GetStringSimilarity(String strA, String strB){
        return LevenshteinDistance.ld(strA, strB);
    }
    
    // ����strA��strB��LCS���ȣ���̬�滮�������ǵݹ顣ʹ�ò�����LCS��MarkArray��LenghthArray��Ա
    // <returns>����LCS����</returns>
	private int LCSLength(List<String> strA, List<String> strB){ // ע���������������ȱ�ݣ��ᵼ����
		if (strA.size() == 0 || strB.size() == 0) {
			return 0;
		}
		// ��ʼ��������ά����Ľṹ
		this.pointerArray = new char[strA.size() + 1][strB.size() + 1];
		this.lengthArray = new int[strA.size() + 1][strB.size() + 1];
		int i, j;
		// �õ�һ�к͵�һ��ֵ
		for (j = 0; j < strB.size() + 1; j++) {
			this.lengthArray[0][j] = 0;
			this.pointerArray[0][j] = '\0';
		}
		for (i = 0; i < strA.size() + 1; i++) {
			this.lengthArray[i][0] = 0;
			this.pointerArray[i][0] = '\0';
		}
		// ��ʼɨ��
		for (i = 1; i < strA.size() + 1; i++) {
			String a = strA.get(i - 1);
			a = TrimChars(a); // ����ո񣬻س����Ʊ�����ַ�
			for (j = 1; j < strB.size() + 1; j++) {
				String b = strB.get(j - 1);
				b = TrimChars(b);
				// ������ȫ��ͬ������ȫ��ͬ�������ƶȸ�����ֵ
				if (a.equals(b)
						|| GetStringSimilarity(a, b) >= Diff.StrSimThreshold) {
					this.pointerArray[i][j] = 'Y';
					this.lengthArray[i][j] = this.lengthArray[i - 1][j - 1] + 1;
				} else {
					if (this.lengthArray[i - 1][j] >= this.lengthArray[i][j - 1]) {
						this.pointerArray[i][j] = 'U';
						this.lengthArray[i][j] = this.lengthArray[i - 1][j];
					} else {
						this.pointerArray[i][j] = 'L';
						this.lengthArray[i][j] = this.lengthArray[i][j - 1];
					}
				}
			}
		}
		return this.lengthArray[strA.size()][strB.size()];
	}
}



public class Diff {

	public static float StrSimThreshold;   //�����ַ������У����ƶ���ֵ
    public static void SetStrSimThreshode(float value){ //�趨���ƶ���ֵ
    	StrSimThreshold = value; 
    }
	public static void UseDefaultStrSimTh(){     //ʹ��Ĭ�����ƶ���ֵ
		StrSimThreshold = (float)0.5; 
	}

    public enum ConfictType{
        MODIFIED,
        ADD,
        DELETE;
    }
    
    class ConfictItem{
        //��ͻ�����ͣ����ADD����contentAΪ�գ����ΪDELETE����contentBΪ�գ����ΪMODIFIED�����߶�����
        public ConfictType type;
        public List<String> contentA;
        public List<String> contentB;
    }

    class DiffInfo extends ArrayList { } //����DiffInfo����
    
    public float FineFragment(List<String> fileA, List<String> fileB){//A�ǿ�¡����Ƭ�Σ�B�ǿ�¡�����ļ�
    	ListStringLCS lcsObject = new ListStringLCS(fileA, fileB); // ����һ��LCS��Ķ���
		lcsObject.GetLCS();
		int minLineB=lcsObject.lcs.get(0).lintOfFileB,maxLineB=0;
		for (LCSItem lcsItem : lcsObject.lcs) {
			if(lcsItem.lintOfFileB < minLineB) 
				minLineB = lcsItem.lintOfFileB;
			if(lcsItem.lintOfFileB > maxLineB)
				maxLineB = lcsItem.lintOfFileB;
		}
		fileB = CreateCRDInfo.GetCFSourceFromCRDInfo(fileB, minLineB, maxLineB);
		return FileSimilarity(DiffFiles(fileA, fileB), 
				fileA.size(), fileB.size(), true);
    }
    
	public DiffInfo DiffFiles(List<String> fileA, List<String> fileB) {
		
		DiffInfo diffFile = new DiffInfo();

		ListStringLCS lcsObject = new ListStringLCS(fileA, fileB); // ����һ��LCS��Ķ���
		lcsObject.GetLCS();
		int lineNoA, lineNoB, prevLineNoA, prevLineNoB;
		prevLineNoA = 0;
		prevLineNoB = 0;
		lineNoA = 0;
		lineNoB = 0;
		// ��LCS�е����ͻ�����μ���diffFile��
		for (LCSItem lcsItem : lcsObject.lcs) {
			int lcsIndex = lcsObject.lcs.indexOf(lcsItem);
			lineNoA = lcsItem.lineOfFileA;
			lineNoB = lcsItem.lintOfFileB;
			if (lineNoA - prevLineNoA == 1 && lineNoB - prevLineNoB == 1) {
			} 
			else if (lineNoA - prevLineNoA == 1 && lineNoB - prevLineNoB > 1){// ���ӵ����
			
				ConfictItem cItem = new ConfictItem();
				cItem.type = ConfictType.ADD;
				cItem.contentA = null;
				cItem.contentB = new ArrayList<String>();
				for (int i = prevLineNoB; i < lineNoB - 1; i++) { // ��fileB�����ӵ��м������
					cItem.contentB.add(fileB.get(i));
				}
				diffFile.add(cItem);
			}
			else if (lineNoA - prevLineNoA > 1 && lineNoB - prevLineNoB == 1){ // ɾ�������
			
				ConfictItem cItem = new ConfictItem();
				cItem.type = ConfictType.DELETE;
				cItem.contentA = new ArrayList<String>();
				cItem.contentB = null;
				for (int i = prevLineNoA; i < lineNoA - 1; i++) { // ��fileA��ɾ�����м������
					cItem.contentA.add(fileA.get(i));
				}
				diffFile.add(cItem);
			}
			// lineNoA - prevLineNoA > 1 && lineNoB - prevLineNoB > 1��ͬʱ�����Ӻ�ɾ��
			else {
				// ���Ⱥ�˳�����ߵĳ�ͻ�����diffFile
				// �����֧�������ǽ��кſ�ǰ��һ�߹���ĳ�ͻ���ȼ���diffFile
				if (prevLineNoA >= prevLineNoB) {
					// �ȼ������ɾ����
					ConfictItem cItem = new ConfictItem();
					cItem.type = ConfictType.DELETE;
					cItem.contentA = new ArrayList<String>();
					cItem.contentB = null;
					for (int i = prevLineNoA; i < lineNoA - 1; i++) // ��fileA��ɾ�����м���contentA
					{
						cItem.contentA.add(fileA.get(i));
					}
					diffFile.add(cItem);
					// ������ұ�������
					cItem = new ConfictItem();
					cItem.type = ConfictType.ADD;
					cItem.contentA = null;
					cItem.contentB = new ArrayList<String>();
					for (int i = prevLineNoB; i < lineNoB - 1; i++) {
						cItem.contentB.add(fileB.get(i));
					} // ��fileB�����ӵ��м���contentB
					diffFile.add(cItem);
				} else {
					// �ȼ����ұ�������
					ConfictItem cItem = new ConfictItem();
					cItem.type = ConfictType.ADD;
					cItem.contentA = null;
					cItem.contentB = new ArrayList<String>();
					for (int i = prevLineNoB; i < lineNoB - 1; i++) {
						cItem.contentB.add(fileB.get(i));
					}
					diffFile.add(cItem);
					// �ȼ������ɾ����
					cItem = new ConfictItem();
					cItem.type = ConfictType.DELETE;
					cItem.contentB = null;
					cItem.contentA = new ArrayList<String>();
					for (int i = prevLineNoA; i < lineNoA - 1; i++) {
						cItem.contentA.add(fileA.get(i));
					}
					diffFile.add(cItem);
				}

			}
			// ��LCS�е������diffFile
			if (lcsItem.isExactSame){ // ��ȫ��ͬ����ֱ�Ӽ���diffFile
			
				diffFile.add(lcsItem.lineContent.toString());
				prevLineNoA = lineNoA; // prevָ������
				prevLineNoB = lineNoB;
			} else{// ���޸ĵ�����Ϊ��ͻ�����diffFile
			
				ConfictItem cItem = new ConfictItem();
				cItem.type = ConfictType.MODIFIED;
				cItem.contentA = new ArrayList<String>();
				cItem.contentB = new ArrayList<String>();
				do{	// ʹ��ѭ�������������޸ĵ���ϲ���һ����ͻ��
					
					// �ָ���ǰ������ݼ���contentA����������ݼ���contentB
					int indexDiv = lcsItem.lineContent.toString().indexOf(
							"$DIVIDER$");
					cItem.contentA.add(lcsItem.lineContent.toString()
							.substring(0, indexDiv));
					cItem.contentB.add(lcsItem.lineContent.toString()
							.substring(indexDiv + 9));
					diffFile.add(cItem); // ����ͻ�����diffFile
					prevLineNoA = lineNoA; // prevָ������
					prevLineNoB = lineNoB;
				} while (lineNoA - prevLineNoA == 1
						&& lineNoB - prevLineNoB == 1 && !lcsItem.isExactSame);
			}

		}
		// ����ʣ����
		if (lineNoA < fileA.size()){ // �������ɾ����
		
			ConfictItem cItem = new ConfictItem();
			cItem.type = ConfictType.DELETE;
			cItem.contentA = new ArrayList<String>();
			cItem.contentB = null;
			for (int i = lineNoA; i < fileA.size(); i++) {
				cItem.contentA.add(fileA.get(i));
			}
			diffFile.add(cItem);
		}
		if (lineNoB < fileB.size()){ // �������������
			ConfictItem cItem = new ConfictItem();
			cItem.type = ConfictType.ADD;
			cItem.contentA = null;
			cItem.contentB = new ArrayList<String>();
			for (int i = lineNoB; i < fileB.size(); i++) {
				cItem.contentB.add(fileB.get(i));
			}
		}

		return diffFile;
	}

	// ��̬�����ļ����ƶȷ���������diffFile��Ϣ����ΪdiffFile�в��ṩ�����ļ��ĳ��ȣ�����ò����ṩ
    public static float FileSimilarity(DiffInfo diffFile, int lengthA, int lengthB, boolean ignoreEmptyLine){
        int uniLineCountA = 0;
        int uniLineCountB = 0;
        int emptyLineCount = 0;
        
        for (Object cItem : diffFile){   
            if (cItem instanceof ConfictItem){ 
                if (((ConfictItem)cItem).contentA != null){
                    for (String line : ((ConfictItem)cItem).contentA){   
                        uniLineCountA++;    //ͳ��FileA�г�ͻ�������
                        if (line.trim() == "") { emptyLineCount++; }   //ͳ�ƿ��е�����
                    }
                }
                if (((ConfictItem)cItem).contentB != null){
                    for (String line : ((ConfictItem)cItem).contentB){
                        uniLineCountB++;    //ͳ��FileB�г�ͻ������� 
                        if (line.trim() == "")
                        { emptyLineCount++; }
                    }
                }
            }
        }
        if (ignoreEmptyLine)
        { return (float)1 - ((float)(uniLineCountA + uniLineCountB - emptyLineCount) / (float)(lengthA + lengthB - emptyLineCount)); }
        else
        { return (float)1 - ((float)(uniLineCountA + uniLineCountB) / (float)(lengthA + lengthB)); }
    }

	
}
