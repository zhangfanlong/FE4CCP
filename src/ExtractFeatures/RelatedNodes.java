package ExtractFeatures;

public class RelatedNodes { 
	public static enum relevantNode{
		//������������Ҫ����������֮ǰͳ�ƹ�����Ҳ����Ҫ
		this_or_super,
		assignment, //���и�ֵ���
		//identifier, //���б���
		literal,//���г���(String,char,int,boolean,null)
		
		if_then_statement,
		if_then_else_statement,
		switch_statement,
		
		while_statement,
		do_statement,
		for_statement,
	}
}
