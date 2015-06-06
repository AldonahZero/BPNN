package com.macb.main;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartUtilities;

import com.macb.bp.BP;
import com.macb.chart.ResultChart;
import com.macb.file.CalFeatureThread;
import com.macb.file.FileUtils;
import com.macb.image.ImageProcess;
import com.macb.utils.AI;
import com.macb.utils.Utils;

public class MainFrame {

	private String fileName = null;

	private Image srcimage = null;
	private Image dstimage = null;

	private Canvas srccanvas;
	private Canvas dstcanvas;

	private Button buttonScane;
	private Button buttonRotate;
	private Button buttonCvtGray;
	private Button buttonQuantify;
	private Button buttonInitFeature;
	private Button buttonTrainSample;
	private Button buttonTest;
	private Button buttonLoadImage;
	private Button buttonResult;

	private Button buttonout;
	
	private Label resLabel;

	public static List<String> imagelist;
	public static List<String> sampleimagelist;
	public static List<String> indoorimagelist;
	public static List<String> outdoorimagelist;

	public static boolean isFeatured = false;
	public static boolean isTrained = false;

	public static int TRAIN_TIME = 400;
	public static int SAMPLE_NUMBER = 30;

	public static BP bp = new BP(20, 15, 1);

	public static void main(String[] args) {
		MainFrame frmae = new MainFrame();
	try {
		frmae.open();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	public void init(Display display) {
		imagelist = new FileUtils().getImageList(AI.IMAGE_DB_PATH, AI.ALL);
		indoorimagelist = new FileUtils().getImageList(AI.IMAGE_DB_PATH,
				AI.INDOOR);
		outdoorimagelist = new FileUtils().getImageList(AI.IMAGE_DB_PATH,
				AI.OUTDOOR);
	}

	public void open() throws IOException {
		final Display display = Display.getDefault();
		final Shell shell = new Shell();
		shell.setText("\"���ھ���\"��\"���⾰��\"ͼ�����");
		shell.setSize(835, 490);
		init(display);

		shell.open();

		buttonInitFeature = new Button(shell, SWT.NONE);
		buttonInitFeature.setText("��ʼ��(��һ��)");
		buttonInitFeature.setBounds(10, 5, 85, 25);

		buttonTrainSample = new Button(shell, SWT.NONE);
		buttonTrainSample.setText("BPѵ��(�ڶ���)");
		buttonTrainSample.setBounds(100, 5, 85, 25);

		buttonLoadImage = new Button(shell, SWT.NONE);
		buttonLoadImage.setText("���ѡȡͼƬ");
		buttonLoadImage.setBounds(190, 5, 80, 25);

		buttonTest = new Button(shell, SWT.NONE);
		buttonTest.setText("����");
		buttonTest.setBounds(280, 5, 80, 25);

		buttonResult = new Button(shell, SWT.NONE);
		buttonResult.setText("���ͳ��");
		buttonResult.setBounds(370, 5, 80, 25);

		buttonScane = new Button(shell, SWT.NONE);
		buttonScane.setText("�ֶ�ѡȡͼƬ");
		buttonScane.setBounds(460, 5, 80, 25);



		buttonCvtGray = new Button(shell, SWT.NONE);
		buttonCvtGray.setText("�Ҷ�");
		buttonCvtGray.setBounds(550, 5, 80, 25);

		buttonQuantify = new Button(shell, SWT.NONE);
		buttonQuantify.setText("����");
		buttonQuantify.setBounds(640, 5, 80, 25);
		
		
		buttonout=new Button(shell,SWT.FLAT);
		buttonout.setText("�˳�ϵͳ");
		buttonout.setBounds(730,5,80,25);
		
		
		
		resLabel = new Label(shell, SWT.None);
		resLabel.setBounds(10, 430, 200, 25);

		srccanvas = new Canvas(shell, SWT.FLAT);
		srccanvas.setBounds(10, 40, 384, 384);

		dstcanvas = new Canvas(shell, SWT.FLAT);
		dstcanvas.setBounds(421, 40, 384, 384);

		
		
		
		buttonInitFeature.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				resLabel.setText("����������ʼ��,���Ժ�>>>");
				if (imagelist.size() == 0 | imagelist == null) {
					resLabel.setForeground(new Color(display, 255, 0, 0));
					resLabel.setText("����ͼ�����ݿ�ʧ��...");
					return;
				}
				CalFeatureThread cfthread = new CalFeatureThread(display,
						imagelist);
				cfthread.start();
				try {
					cfthread.join();
					isFeatured = true;
					resLabel.setForeground(new Color(display, 0, 0, 0));
					resLabel.setText("������ʼ�����...");
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		buttonTrainSample.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				resLabel.setText("����ѵ��,���Ժ�>>>");
				if (imagelist.size() == 0 | imagelist == null
						|| isFeatured == false) {
					resLabel.setForeground(new Color(display, 255, 0, 0));
					resLabel.setText("���ȳ�ʼ������...");
					return;
				}
				int[] indeice = new Utils().getRandomArray(1, 100,
						SAMPLE_NUMBER);
				for (int k = 0; k < TRAIN_TIME; k++) { // ѵ��200��
					for (int i = 0; i < indeice.length; i++) {
						double fv[] = new FileUtils()
								.getFeatureFromFile(indoorimagelist
										.get(indeice[i]));
						bp.train(fv, new double[] { 1.0 });
					}
					for (int i = 0; i < indeice.length; i++) {
						double fv[] = new FileUtils()
								.getFeatureFromFile(outdoorimagelist
										.get(indeice[i]));
						bp.train(fv, new double[] { 0.0 });
					}
				}
				resLabel.setText("����ѵ�������Ժ�");
				resLabel.setForeground(new Color(display, 0, 0, 0));
				resLabel.setText("����ѵ�����...");
			}
		});

		buttonLoadImage.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				resLabel.setText("");
				int index = (int) (Math.random() * imagelist.size());
				srcimage = new Image(display, imagelist.get(index));
				srccanvas.redraw();

			}
		});

		buttonTest.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {

				if (srcimage == null) {
					resLabel.setForeground(new Color(display, 255, 0, 0));
					resLabel.setText("��ѡ��Ҫѵ����ͼƬ...");
					return;
				}
				if (isFeatured == false) {
					resLabel.setForeground(new Color(display, 255, 0, 0));
					resLabel.setText("����ѵ��������...");
					return;
				}
				double fv[] = new ImageProcess().getUnitFeature(srcimage
						.getImageData());
				int testResult = new BigDecimal(bp.test(fv)[0]).setScale(0,
						BigDecimal.ROUND_HALF_UP).intValue();
				if (testResult == 1) {
					resLabel.setForeground(new Color(display, 255, 0, 0));
					resLabel.setText("����һ������ͼ...");
				} else {
					resLabel.setForeground(new Color(display, 255, 0, 0));
					resLabel.setText("����һ������ͼ...");
				}
			}
		});
		
		buttonout.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(final SelectionEvent e) {
				
				
			System.exit(0);
				
			}
			
			
			
			
			
			
		});

		buttonResult.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {

				double[] fv = null;
				int testResult = 0;

				int idt = 0;
				int idf = 0;
				int odt = 0;
				int odf = 0;

				FileWriter fw = null;
				BufferedWriter bw = null;
				try {
					fw = new FileWriter("log.txt", false);
					bw = new BufferedWriter(fw);
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				for (int i = 0; i < imagelist.size(); i++) {

					Image img = new Image(display, imagelist.get(i));
					fv = new ImageProcess().getUnitFeature(img.getImageData());
					testResult = new BigDecimal(bp.test(fv)[0]).setScale(0,
							BigDecimal.ROUND_HALF_UP).intValue();
					try {
						bw
								.write(imagelist.get(i)
										+ (testResult == 1 ? "---Indoor"
												: "---Outdoor"));
						bw.newLine();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (imagelist.get(i).contains("Indoor")) {
						if (testResult == 1) {
							idt++;
						} else {
							idf++;
						}
					} else {
						if (testResult == 0) {
							odt++;
						} else {
							odf++;
						}
					}
				}
				try {
					bw.flush();
					bw.close();
					fw.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				// System.out.println(idt + " " + idf + " " + odt + " " + odf);

				ResultChart rc = new ResultChart(
						new int[] { idt, idf, odt, odf });

				try {
					OutputStream os = new FileOutputStream("result.jpeg");
					ChartUtilities
							.writeChartAsJPEG(os, rc.getChart(), 384, 290);
					dstimage = new Image(display, "result.jpeg");
					dstcanvas.redraw();
				} catch (Exception e1) {

					e1.printStackTrace();
				}
			}
		});
		buttonScane.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {

				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				resLabel.setText("");
				dlg.setText("Open");
				dlg.setFilterNames(new String[] { "ͼƬ�ļ�(*.jpg)",
								"ͼƬ�ļ���*.gif��" });
				dlg.setFilterExtensions(new String[] { "*.jpg", "*.gif",
						"*.png" });
				fileName = dlg.open();
				if (fileName != null) {
					srcimage = new Image(display, fileName);
					srccanvas.redraw();
				}
			}
		});

	

		buttonCvtGray.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				if (srcimage == null)
					return;
				ImageData srcData = srcimage.getImageData();
				srcData = new ImageProcess().toGrayImage(srcData);
				dstimage = new Image(display, srcData);
				dstcanvas.redraw();
			}
		});

		buttonQuantify.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				if (srcimage == null)
					return;
				ImageData srcData = srcimage.getImageData();
				srcData = new ImageProcess().toQuantifiedImage(srcData);
				dstimage = new Image(display, srcData);
				dstcanvas.redraw();
			}
		});

		srccanvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				if (srcimage != null) {
					if (srcimage.getBounds().height > srcimage.getBounds().width)
						arg0.gc.drawImage(srcimage,
								(384 - srcimage.getBounds().width) / 2, 0);
					else {
						arg0.gc.drawImage(srcimage, 0, (384 - srcimage
								.getBounds().height) / 2);
					}
				}
			}
		});

		dstcanvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				if (dstimage != null) {
					if (dstimage.getBounds().height > dstimage.getBounds().width)
						arg0.gc.drawImage(dstimage,
								(384 - dstimage.getBounds().width) / 2, 0);
					else {
						arg0.gc.drawImage(dstimage, 0, (384 - dstimage
								.getBounds().height) / 2);
					}
				}
			}
		});

		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
