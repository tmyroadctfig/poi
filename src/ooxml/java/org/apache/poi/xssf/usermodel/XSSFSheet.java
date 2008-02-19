/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.xssf.usermodel;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.util.PaneInformation;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Patriarch;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.helpers.ColumnHelper;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBreak;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCols;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageBreak;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSelection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetFormatPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;


public class XSSFSheet implements Sheet {

    private CTSheet sheet;
    private CTWorksheet worksheet;
    private List<Row> rows;
    private ColumnHelper columnHelper;
    private XSSFWorkbook workbook;

    public static final short LeftMargin = 0;
    public static final short RightMargin = 1;
    public static final short TopMargin = 2;
    public static final short BottomMargin = 3;
    public static final short HeaderMargin = 4;
    public static final short FooterMargin = 5;
    
    public XSSFSheet(CTSheet sheet, XSSFWorkbook workbook) {
        this.sheet = sheet;
        this.worksheet = CTWorksheet.Factory.newInstance();
        this.workbook = workbook;
        this.worksheet.addNewSheetData();
        initRows(worksheet);
        
        this.worksheet.addNewHeaderFooter();
        worksheet.addNewRowBreaks();
        worksheet.addNewColBreaks();
        CTSheetPr sheetPr = worksheet.addNewSheetPr();
        sheetPr.addNewPageSetUpPr();
        worksheet.addNewPageMargins();
        
        // XXX Initial default data, probably useful only for testing. Review and eliminate if necessary.
        CTSheetViews views = this.worksheet.addNewSheetViews();
        CTSheetView view = views.addNewSheetView();
        view.setWorkbookViewId(0);
        view.setZoomScale(100);
        CTSelection selection = view.addNewSelection();
        selection.setActiveCell("A1");
        CTSheetFormatPr format = this.worksheet.addNewSheetFormatPr();
        format.setDefaultColWidth(13);
        format.setDefaultRowHeight(15);
        format.setCustomHeight(true);
        CTCols cols = this.worksheet.addNewCols();
        CTCol col = cols.addNewCol();
        col.setMin(1);
        col.setMax(2);
        col.setWidth(13);
        col.setCustomWidth(true);
        for (int i = 3 ; i < 5 ; ++i) {
            col = cols.addNewCol();
            col.setMin(i);
            col.setMax(i);
            col.setWidth(13);
            col.setCustomWidth(true);
        }

        initColumns(worksheet);
    }
    
    public XSSFSheet(CTSheet sheet, CTWorksheet worksheet, XSSFWorkbook workbook) {
        this.sheet = sheet;
        this.worksheet = worksheet;
        this.workbook = workbook;
        initRows(worksheet);
        initColumns(worksheet);
    }

    public XSSFSheet(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public XSSFWorkbook getWorkbook() {
        return this.workbook;
    }

    private void initRows(CTWorksheet worksheet) {
        this.rows = new LinkedList<Row>();
        for (CTRow row : worksheet.getSheetData().getRowArray()) {
            this.rows.add(new XSSFRow(row, this));
        }
    }

    private void initColumns(CTWorksheet worksheet) {
        columnHelper = new ColumnHelper(worksheet);
    }

    protected CTSheet getSheet() {
        return this.sheet;
    }
    
    protected CTWorksheet getWorksheet() {
        return this.worksheet;
    }
    
    public int addMergedRegion(Region region) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void autoSizeColumn(short column) {
        // TODO Auto-generated method stub

    }

    public Patriarch createDrawingPatriarch() {
        // TODO Auto-generated method stub
        return null;
    }

    public void createFreezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        // TODO Auto-generated method stub

    }

    public void createFreezePane(int colSplit, int rowSplit) {
        // TODO Auto-generated method stub

    }

    protected XSSFRow addRow(int index, int rownum) {
        CTRow row = this.worksheet.getSheetData().insertNewRow(index);
        XSSFRow xrow = new XSSFRow(row, this);
        xrow.setRowNum(rownum);
        return xrow;
    }

    public Row createRow(int rownum) {
        int index = 0;
        for (Row r : this.rows) {
                if (r.getRowNum() == rownum) {
                        // Replace r with new row
                XSSFRow xrow = addRow(index, rownum);
                        rows.set(index, xrow);
                        return xrow;
                }
                if (r.getRowNum() > rownum) {
                        XSSFRow xrow = addRow(index, rownum);
                        rows.add(index, xrow);
                        return xrow;
                }
                ++index;
        }
        XSSFRow xrow = addRow(index, rownum);
        rows.add(xrow);
        return xrow;
    }

    public void createSplitPane(int splitPos, int splitPos2, int leftmostColumn, int topRow, int activePane) {
        // TODO Auto-generated method stub

    }

    public void dumpDrawingRecords(boolean fat) {
        // TODO Auto-generated method stub

    }

    public boolean getAlternateExpression() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getAlternateFormula() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getAutobreaks() {
        return worksheet.getSheetPr().getPageSetUpPr().getAutoPageBreaks();
    }

    public Comment getCellComment(int row, int column) {
        // TODO Auto-generated method stub
        return null;
    }

    public short[] getColumnBreaks() {
        CTBreak[] brkArray = worksheet.getColBreaks().getBrkArray();
        if (brkArray.length == 0) {
            return null;
        }
        short[] breaks = new short[brkArray.length];
        for (int i = 0 ; i < brkArray.length ; i++) {
            CTBreak brk = brkArray[i];
            breaks[i] = (short) brk.getId();
        }
        return breaks;
    }

    public short getColumnWidth(short column) {
        return (short) columnHelper.getColumn(column).getWidth();
    }

    public short getDefaultColumnWidth() {
        return (short) this.worksheet.getSheetFormatPr().getDefaultColWidth();
    }

    public short getDefaultRowHeight() {
        return (short) (this.worksheet.getSheetFormatPr().getDefaultRowHeight() * 20);
    }

    public float getDefaultRowHeightInPoints() {
        return (short) this.worksheet.getSheetFormatPr().getDefaultRowHeight();
    }

    public boolean getDialog() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getDisplayGuts() {
        // TODO Auto-generated method stub
        return false;
    }

    public int getFirstRowNum() {
        for (Iterator<Row> it = rowIterator() ; it.hasNext() ; ) {
            Row row = it.next();
            if (row != null) {
                return row.getRowNum();
            }
        }
        return -1;
    }

    public boolean getFitToPage() {
        return worksheet.getSheetPr().getPageSetUpPr().getFitToPage();
    }

    public Footer getFooter() {
        return getOddFooter();
    }
    
    public Footer getOddFooter() {
        return new XSSFOddFooter(worksheet.getHeaderFooter());
    }
    
    public Footer getEvenFooter() {
        return new XSSFEvenFooter(worksheet.getHeaderFooter());
    }
    
    public Footer getFirstFooter() {
        return new XSSFFirstFooter(worksheet.getHeaderFooter());
    }

    public Header getHeader() {
        return getOddHeader();
    }
    
    public Header getOddHeader() {
        return new XSSFOddHeader(worksheet.getHeaderFooter());
    }
    
    public Header getEvenHeader() {
        return new XSSFEvenHeader(worksheet.getHeaderFooter()
);
    }
    
    public Header getFirstHeader() {
        return new XSSFFirstHeader(worksheet.getHeaderFooter());
    }

    public boolean getHorizontallyCenter() {
        // TODO Auto-generated method stub
        return false;
    }

    public int getLastRowNum() {
        int lastRowNum = -1;
        for (Iterator<Row> it = rowIterator() ; it.hasNext() ; ) {
            Row row = it.next();
            if (row != null) {
                lastRowNum = row.getRowNum();
            }
        }
        return lastRowNum;
    }

    public short getLeftCol() {
        // TODO Auto-generated method stub
        return 0;
    }

    public double getMargin(short margin) {
        CTPageMargins pageMargins = worksheet.getPageMargins();
        switch (margin) {
        case LeftMargin:
            return pageMargins.getLeft();
        case RightMargin:
            return pageMargins.getRight();
        case TopMargin:
            return pageMargins.getTop();
        case BottomMargin:
            return pageMargins.getBottom();
        case HeaderMargin:
            return pageMargins.getHeader();
        case FooterMargin:
            return pageMargins.getFooter();
        default :
            throw new RuntimeException( "Unknown margin constant:  " + margin );
        }
    }

    public Region getMergedRegionAt(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getNumMergedRegions() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean getObjectProtect() {
        // TODO Auto-generated method stub
        return false;
    }

    public PaneInformation getPaneInformation() {
        // TODO Auto-generated method stub
        return null;
    }

    public short getPassword() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getPhysicalNumberOfRows() {
        int counter = 0;
        for (Iterator<Row> it = rowIterator() ; it.hasNext() ; ) {
            if (it.next() != null) {
                counter++;
            }
        }
        return counter;
    }

    public PrintSetup getPrintSetup() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getProtect() {
        // TODO Auto-generated method stub
        return false;
    }

    public Row getRow(int rownum) {
        for (Iterator<Row> it = rowIterator() ; it.hasNext() ; ) {
                Row row = it.next();
                if (row.getRowNum() == rownum) {
                        return row;
                }
        }
        return null;
    }

    public int[] getRowBreaks() {
        CTPageBreak rowBreaks = worksheet.getRowBreaks();
        int breaksCount = rowBreaks.getBrkArray().length;
        if (breaksCount == 0) {
            return null;
        }
        int[] breaks = new int[breaksCount];
        for (int i = 0 ; i < breaksCount ; i++) {
            CTBreak brk = rowBreaks.getBrkArray(i);
            breaks[i] = (int) brk.getId();
        }
        return breaks;
    }

    public boolean getRowSumsBelow() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getRowSumsRight() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getScenarioProtect() {
        // TODO Auto-generated method stub
        return false;
    }

    public short getTopRow() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean getVerticallyCenter(boolean value) {
        // TODO Auto-generated method stub
        return false;
    }

    public void groupColumn(short fromColumn, short toColumn) {
        // TODO Auto-generated method stub

    }

    public void groupRow(int fromRow, int toRow) {
        // TODO Auto-generated method stub

    }

    public boolean isColumnBroken(short column) {
        CTBreak[] brkArray = worksheet.getColBreaks().getBrkArray();
        for (int i = 0 ; i < brkArray.length ; i++) {
            if (brkArray[i].getId() == column) {
                return true;
            }
        }
        return false;
    }

    public boolean isColumnHidden(short column) {
        return columnHelper.getColumn(column).getHidden();
    }

    public boolean isDisplayFormulas() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isDisplayGridlines() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isDisplayRowColHeadings() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isGridsPrinted() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isPrintGridlines() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRowBroken(int row) {
        int[] rowBreaks = getRowBreaks();
        if (rowBreaks == null) {
            return false;
        }
        for (int i = 0 ; i < rowBreaks.length ; i++) {
            if (rowBreaks[i] == row) {
                return true;
            }
        }
        return false;
    }

    public void protectSheet(String password) {
        // TODO Auto-generated method stub

    }

    public void removeColumnBreak(short column) {
        CTBreak[] brkArray = worksheet.getColBreaks().getBrkArray();
        for (int i = 0 ; i < brkArray.length ; i++) {
            if (brkArray[i].getId() == column) {
                worksheet.getColBreaks().removeBrk(i);
                continue;
            }
        }
    }

    public void removeMergedRegion(int index) {
        // TODO Auto-generated method stub

    }

    public void removeRow(Row row) {
        int counter = 0;
        for (Iterator<Row> it = rowIterator() ; it.hasNext() ; ) {
            Row r = it.next();
            if (r.getRowNum() == row.getRowNum()) {
                it.remove();
                worksheet.getSheetData().removeRow(counter);
            }
            counter++;
        }
    }

    public void removeRowBreak(int row) {
        CTBreak[] brkArray = worksheet.getRowBreaks().getBrkArray();
        for (int i = 0 ; i < brkArray.length ; i++) {
            if (brkArray[i].getId() == row) {
                worksheet.getRowBreaks().removeBrk(i);
                continue;
            }
        }
    }

    public Iterator<Row> rowIterator() {
        return rows.iterator();
    }
    /**
     * Alias for {@link #rowIterator()} to
     *  allow foreach loops
     */
    public Iterator<Row> iterator() {
    	return rowIterator();
    }

    public void setAlternativeExpression(boolean b) {
        // TODO Auto-generated method stub

    }

    public void setAlternativeFormula(boolean b) {
        // TODO Auto-generated method stub

    }

    public void setAutobreaks(boolean b) {
        worksheet.getSheetPr().getPageSetUpPr().setAutoPageBreaks(b);
    }

    public void setColumnBreak(short column) {
        if (! isColumnBroken(column)) {
            CTBreak brk = worksheet.getColBreaks().addNewBrk();
            brk.setId(column);
        }
    }

    public void setColumnGroupCollapsed(short columnNumber, boolean collapsed) {
        // TODO Auto-generated method stub

    }

    public void setColumnHidden(short column, boolean hidden) {
        columnHelper.getColumn(column).setHidden(hidden);
    }

    public void setColumnWidth(short column, short width) {
        columnHelper.getColumn(column).setWidth(width);
    }

    public void setDefaultColumnStyle(short column, CellStyle style) {
        // TODO Auto-generated method stub

    }

    public void setDefaultColumnWidth(short width) {
        this.worksheet.getSheetFormatPr().setDefaultColWidth((double) width);
    }

    public void setDefaultRowHeight(short height) {
        this.worksheet.getSheetFormatPr().setDefaultRowHeight(height / 20);

    }

    public void setDefaultRowHeightInPoints(float height) {
        this.worksheet.getSheetFormatPr().setDefaultRowHeight(height);

    }

    public void setDialog(boolean b) {
        // TODO Auto-generated method stub

    }

    public void setDisplayFormulas(boolean show) {
        // TODO Auto-generated method stub

    }

    public void setDisplayGridlines(boolean show) {
        // TODO Auto-generated method stub

    }

    public void setDisplayGuts(boolean b) {
        // TODO Auto-generated method stub

    }

    public void setDisplayRowColHeadings(boolean show) {
        // TODO Auto-generated method stub

    }

    public void setFitToPage(boolean b) {
        worksheet.getSheetPr().getPageSetUpPr().setFitToPage(b);
    }

    public void setGridsPrinted(boolean value) {
        // TODO Auto-generated method stub

    }

    public void setHorizontallyCenter(boolean value) {
        // TODO Auto-generated method stub

    }

    public void setMargin(short margin, double size) {
        CTPageMargins pageMargins = worksheet.getPageMargins();
        switch (margin) {
        case LeftMargin:
            pageMargins.setLeft(size);
        case RightMargin:
            pageMargins.setRight(size);
        case TopMargin:
            pageMargins.setTop(size);
        case BottomMargin:
            pageMargins.setBottom(size);
        case HeaderMargin:
            pageMargins.setHeader(size);
        case FooterMargin:
            pageMargins.setFooter(size);
        }
    }

    public void setPrintGridlines(boolean newPrintGridlines) {
        // TODO Auto-generated method stub

    }

    public void setProtect(boolean protect) {
        // TODO Auto-generated method stub

    }

    public void setRowBreak(int row) {
        CTPageBreak pageBreak = worksheet.getRowBreaks();
        if (! isRowBroken(row)) {
            CTBreak brk = pageBreak.addNewBrk();
            brk.setId(row);
        }
    }

    public void setRowGroupCollapsed(int row, boolean collapse) {
        // TODO Auto-generated method stub

    }

    public void setRowSumsBelow(boolean b) {
        // TODO Auto-generated method stub

    }

    public void setRowSumsRight(boolean b) {
        // TODO Auto-generated method stub

    }

    public void setSelected(boolean sel) {
        // TODO Auto-generated method stub

    }

    public void setVerticallyCenter(boolean value) {
        // TODO Auto-generated method stub

    }

    public void setZoom(int numerator, int denominator) {
        // TODO Auto-generated method stub

    }

    public void shiftRows(int startRow, int endRow, int n) {
        // TODO Auto-generated method stub

    }

    public void shiftRows(int startRow, int endRow, int n, boolean copyRowHeight, boolean resetOriginalRowHeight) {
        // TODO Auto-generated method stub

    }

    public void showInPane(short toprow, short leftcol) {
        // TODO Auto-generated method stub

    }

    public void ungroupColumn(short fromColumn, short toColumn) {
        // TODO Auto-generated method stub

    }

    public void ungroupRow(int fromRow, int toRow) {
        // TODO Auto-generated method stub

    }

    public void setTabSelected(boolean flag) {
        CTSheetViews views = this.worksheet.getSheetViews();
        for (CTSheetView view : views.getSheetViewArray()) {
            view.setTabSelected(flag);
        }
    }
    
    public boolean isTabSelected() {
        CTSheetView view = getDefaultSheetView();
        return view != null && view.getTabSelected();
    }

    /**
     * Return the default sheet view. This is the last one if the sheet's views, according to sec. 3.3.1.83
     * of the OOXML spec: "A single sheet view definition. When more than 1 sheet view is defined in the file,
     * it means that when opening the workbook, each sheet view corresponds to a separate window within the 
     * spreadsheet application, where each window is showing the particular sheet. containing the same 
     * workbookViewId value, the last sheetView definition is loaded, and the others are discarded. 
     * When multiple windows are viewing the same sheet, multiple sheetView elements (with corresponding 
     * workbookView entries) are saved."
     */
    private CTSheetView getDefaultSheetView() {
        CTSheetViews views = this.worksheet.getSheetViews();
        if (views == null || views.getSheetViewArray() == null || views.getSheetViewArray().length <= 0) {
            return null;
        }
        return views.getSheetViewArray(views.getSheetViewArray().length - 1);
    }
    
    protected XSSFSheet cloneSheet() {
        return new XSSFSheet((CTSheet) sheet.copy(), this.workbook);
    }

}