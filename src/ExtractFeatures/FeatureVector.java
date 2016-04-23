package ExtractFeatures;

public class FeatureVector {
	
	//代码特征
	private int sourceLine;//克隆组代码行数
	private int fragCount; //克隆组数量

	//四个heathed度量
	private int uniOPERATORCount;//唯一操作符数量，即操作符种类
    private int uniOperandCount;//唯一操作数数量，即操作数种类
    private int totalOPERATORCount;//操作符总量
    private int totalOperandCount;//操作数总量   
    
    //函数信息
  	private int totalMethodInvocCount;//函数调用
  	private int libraryMethodInvocCount;//库函数调用
  	private int localMethodInvocCount;//本地函数调用
  	private int otherMethodInvocCount;//其他调用
  	private int totalParameterCount;//总参数个数
  	
  	//目的特征
  	private boolean isLocalClone;//是否是函数克隆
  	private float simFileName;//文件名相似度
  	private float simMaskedFileName;//文件名相似度MASK
  	
  	private float simMethodName;//方法名相似度
  	private float simTotalParaName;//参数名相似度之和
  	private float simMaxParaName;//最大参数名相似度
  	private float simTotalParaType;//
  	
  	private boolean isSameBlockInfo;//..................
  	private double simCloneFragments;//..................
  	
  	//结构特征
  	private int[] struFeature;
	private int consisCount;
  	//一致性维护标签
  	private int consistence;
  	
  	
  	//历史变化序列统计
  	private int[] evoPattern;
  	private int age;
  	private int [] lastevoPattern;
  	
  	public int[] getlastevoPattern() {
		return lastevoPattern;
	}
	public void setlastevoPattern(int[] evoPattern) {
		this.lastevoPattern = new int[6];
		this.lastevoPattern = evoPattern;
	}
  	
  	public int[] getEvoPattern() {
		return evoPattern;
	}
	public void setEvoPattern(int[] evoPattern) {
		this.evoPattern = new int[6];
		this.evoPattern = evoPattern;
	}
	public int getSourceLine() {
		return sourceLine;
	}
	public void setSourceLine(int sourceLine) {
		this.sourceLine = sourceLine;
	}
	
	public int getFragCount() {
		return fragCount;
	}
	public void setFragCount(int fragCount) {
		this.fragCount = fragCount;
	}
	
	
	public int getUniOPERATORCount() {
		return uniOPERATORCount;
	}
	public void setUniOPERATORCount(int uniOPERATORCount) {
		this.uniOPERATORCount = uniOPERATORCount;
	}
	public int getUniOperandCount() {
		return uniOperandCount;
	}
	public void setUniOperandCount(int uniOperandCount) {
		this.uniOperandCount = uniOperandCount;
	}
	public int getTotalOPERATORCount() {
		return totalOPERATORCount;
	}
	public void setTotalOPERATORCount(int totalOPERATORCount) {
		this.totalOPERATORCount = totalOPERATORCount;
	}
	public int getTotalOperandCount() {
		return totalOperandCount;
	}
	public void setTotalOperandCount(int totalOperandCount) {
		this.totalOperandCount = totalOperandCount;
	}
	public int getTotalMethodInvocCount() {
		return totalMethodInvocCount;
	}
	public void setTotalMethodInvocCount(int totalMethodInvocCount) {
		this.totalMethodInvocCount = totalMethodInvocCount;
	}
	public int getLibraryMethodInvocCount() {
		return libraryMethodInvocCount;
	}
	public void setLibraryMethodInvocCount(int libraryMethodInvocCount) {
		this.libraryMethodInvocCount = libraryMethodInvocCount;
	}
	public int getLocalMethodInvocCount() {
		return localMethodInvocCount;
	}
	public void setLocalMethodInvocCount(int localMethodInvocCount) {
		this.localMethodInvocCount = localMethodInvocCount;
	}
	public int getOtherMethodInvocCount() {
		return otherMethodInvocCount;
	}
	public void setOtherMethodInvocCount(int otherMethodInvocCount) {
		this.otherMethodInvocCount = otherMethodInvocCount;
	}
	public int getTotalParameterCount() {
		return totalParameterCount;
	}
	public void setTotalParameterCount(int totalParameterCount) {
		this.totalParameterCount = totalParameterCount;
	}
	public boolean isLocalClone() {
		return isLocalClone;
	}
	public void setLocalClone(boolean isLocalClone) {
		this.isLocalClone = isLocalClone;
	}
	public float getSimFileName() {
		return simFileName;
	}
	public void setSimFileName(float simFileName) {
		this.simFileName = simFileName;
	}
	public float getSimMaskedFileName() {
		return simMaskedFileName;
	}
	public void setSimMaskedFileName(float simMaskedFileName) {
		this.simMaskedFileName = simMaskedFileName;
	}
	public float getSimMethodName() {
		return simMethodName;
	}
	public void setSimMethodName(float simMethodName) {
		this.simMethodName = simMethodName;
	}
	public float getSimTotalParaName() {
		return simTotalParaName;
	}
	public float getSimMaxParaName() {
		return simMaxParaName;
	}
	public float getSimTotalParaType() {
		return simTotalParaType;
	}
	public void setSimTotalParaName(float simTotalParaName) {
		this.simTotalParaName = simTotalParaName;
	}
	public void setSimMaxParaName(float simMaxParaName) {
		this.simMaxParaName = simMaxParaName;
	}
	public void setSimTotalParaType(float simTotalParaType) {
		this.simTotalParaType = simTotalParaType;
	}
	public boolean getIsSameBlockInfo() {
		return isSameBlockInfo;
	}
	public double getSimCloneFragment() {
		return simCloneFragments;
	}
	public void setIsSameBlockInfo(boolean isSameBlockInfo) {
		this.isSameBlockInfo = isSameBlockInfo;
	}
	public void setSimCloneFragment(double simCloneFragment) {
		this.simCloneFragments = simCloneFragment;
	}
	
	public int[] getStruFeature() {
		return struFeature;
	}
	public void setStruFeature(int[] struFeature) {
		this.struFeature = new int[RelatedNodes.relevantNode.values().length];
		this.struFeature = struFeature;
	}
	public int getConsistence() {
		return consistence;
	}
	public void setConsistence(int consistence) {
		this.consistence = consistence;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getConsisCount() {
		return consisCount;
	}
	public void setConsisCount(int consisCount) {
		this.consisCount = consisCount;
	}
	
	

}
