package MyFrames;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import PreProcess.CreateCRDInfo;
import PreProcess.CreateGenealogyInfo;
import PreProcess.CreateMappingInfo;
import CloneRepresentation.CloneGroup;
import ExtractFeatures.CreateTrainedFeatureVector;
import ExtractFeatures.CreateFeatureVector2;
import Global.Path;
import Global.VariationInformation;

public class MainFrame {

	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		////////////////////////////////�Ժ�ɾ��
/*		Path._subSysDirectory = "C:\\Users\\YueYuan\\Desktop\\SubjectSys_dnsjava";
		Path._clonesFolderPath = "C:\\Users\\YueYuan\\Desktop\\dnsjava-results";
		Path._crdDirectory = "C:\\Users\\YueYuan\\Desktop\\dnsjava-results\\CRDFiles\\blocks";
	*/
		
		//���˵�
		Menu mainMenu=new Menu(shell,SWT.BAR);
		shell.setMenuBar(mainMenu);
		{
			//Import ��
			MenuItem importItem=new MenuItem(mainMenu,SWT.CASCADE);
			importItem.setText("Import");
			//Import �˵�
			Menu importMenu=new Menu(shell,SWT.DROP_DOWN);
			importItem.setMenu(importMenu);
			{
				//Select ��
				MenuItem selectItem=new MenuItem(importMenu,SWT.PUSH);
				selectItem.setText("Select Directory");
				selectItem.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						SelectDirectory selDir=new SelectDirectory(shell,SWT.OK | SWT.CLOSE);
						selDir.open();
					}
				});
				//Ԥ������ -- CRD Map Genealogy
				MenuItem preProceItem = new MenuItem(importMenu,SWT.PUSH);
				preProceItem.setText("PreProcess");
				preProceItem.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						CreateCRDInfo createCRD = new CreateCRDInfo();
						createCRD.CreateCRDForSys();
						
						
						for(int i=0;i<VariationInformation.clonesInVersion.size()-1;i++){
							List<CloneGroup> srcGroupList = new ArrayList<CloneGroup>();
							List<CloneGroup> destGroupList = new ArrayList<CloneGroup>();		
							// ��ȡԴ�汾 Ŀ�İ汾��group
							for(CloneGroup group : VariationInformation.cloneGroup){
								if(group.getVersionID() == i)
									srcGroupList.add(group);
								else if(group.getVersionID() == i+1)
									destGroupList.add(group);
							}
							CreateMappingInfo mapInfo = new CreateMappingInfo();
							mapInfo.MapBetweenVersions(srcGroupList, destGroupList);
							mapInfo.RecognizeEvolutionPattern();
							mapInfo.SaveMappingForSys(i,i+1);//��������ϵͳ��Mapping
						}
						
						
						CreateGenealogyInfo genealogyInfo = new CreateGenealogyInfo();
						genealogyInfo.CreateGenealogyForAll();
						
						CreateFeatureVector2 createFeature = new CreateFeatureVector2();
						createFeature.ExtractFeature();
						MessageDialog.openInformation(shell, null, " ARFF_2 Created!");
						
						/*CreateTrainedFeatureVector createFeature = new CreateTrainedFeatureVector();
						createFeature.ExtractFeature();
						MessageDialog.openInformation(shell, null, "PreProcess Finishen! ARFF Created!");*/
					}
				});
				
				//CRD ��
				MenuItem crdItem=new MenuItem(importMenu,SWT.PUSH);
				crdItem.setText("CRD");
				crdItem.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
							CreateCRDInfo createCRD = new CreateCRDInfo();
							createCRD.CreateCRDForSys();
							
							MessageDialog.openInformation(shell, null, "CRD finished!");
					}
				});
				
				//Map ��
				MenuItem mapItem=new MenuItem(importMenu,SWT.PUSH);
				mapItem.setText("Map");
				mapItem.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {				
						for(int i=0;i<VariationInformation.clonesInVersion.size()-1;i++){
								List<CloneGroup> srcGroupList = new ArrayList<CloneGroup>();
								List<CloneGroup> destGroupList = new ArrayList<CloneGroup>();		
								// ��ȡԴ�汾 Ŀ�İ汾��group
								for(CloneGroup group : VariationInformation.cloneGroup){
									if(group.getVersionID() == i)
										srcGroupList.add(group);
									else if(group.getVersionID() == i+1)
										destGroupList.add(group);
								}
								CreateMappingInfo mapInfo = new CreateMappingInfo();
								mapInfo.MapBetweenVersions(srcGroupList, destGroupList);
								mapInfo.RecognizeEvolutionPattern();
								mapInfo.SaveMappingForSys(i,i+1);//��������ϵͳ��Mapping
								//mapInfo.SaveMappingForGroup(i, i+1);//��ɢMapping
						}
						MessageDialog.openInformation(shell, null, "Mapping finished!");
					}
				});
				
				//������¡��ϵ  ��
				MenuItem GenealogyItem = new MenuItem(importMenu,SWT.PUSH);
				GenealogyItem.setText("CreateGenealogy");
				GenealogyItem.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						CreateGenealogyInfo genealogyInfo = new CreateGenealogyInfo();
						genealogyInfo.CreateGenealogyForAll();
						MessageDialog.openInformation(shell, null, "Genealogy finished!");
					}
				});
				
				//��ȡ��������
				MenuItem featureItem=new MenuItem(importMenu,SWT.PUSH);
				featureItem.setText("ExtractFeature");
				featureItem.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						CreateTrainedFeatureVector createFeature = new CreateTrainedFeatureVector();
						createFeature.ExtractFeature();
						MessageDialog.openInformation(shell, null, "ARFF Created!");
					}
				});
				
				//��ȡ��������
				MenuItem featureItem2=new MenuItem(importMenu,SWT.PUSH);
				featureItem2.setText("TempExtractFeature");
				featureItem2.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						CreateFeatureVector2 createFeature = new CreateFeatureVector2();
						createFeature.ExtractFeature();
						MessageDialog.openInformation(shell, null, " ARFF_2 Created!");
					}
				});
				
				
				//Test��
				MenuItem testItem=new MenuItem(importMenu,SWT.PUSH);
				testItem.setText("WriteGroup");
				testItem.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						//TestWriteGroup.printGroupInfo("C:\\Users\\ZFL\\Desktop\\GroupResult.xml");
						MessageDialog.openInformation(shell, null, "Group finished!");
					}
				});
				
				MenuItem testMapItem=new MenuItem(importMenu,SWT.PUSH);
				testMapItem.setText("WriteMapping");
				testMapItem.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						//TestWriteMapping.printMappingInfo("C:\\Users\\ZFL\\Desktop\\MappingResult.xml");
						MessageDialog.openInformation(shell, null, "Mapping finished!");
					}
				});
				
				MenuItem geneItem=new MenuItem(importMenu,SWT.PUSH);
				geneItem.setText("WriteGenealogy");
				geneItem.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						TestWriteGenealogy.printGenealogyInfo("C:\\Users\\ZFL\\Desktop\\GenealogyResult.xml");
						MessageDialog.openInformation(shell, null, "Genealogy finished!");
					}
				});
			}
		}
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
