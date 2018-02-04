package com.joebotics.simmer.client.gui.dialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.Editable;

import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialModalContent;
import gwt.material.design.client.ui.MaterialTextBox;

public class EditDialog extends Composite {

    private static EditDialogUiBinder uiBinder = GWT.create(EditDialogUiBinder.class);

    interface EditDialogUiBinder extends UiBinder<Widget, EditDialog> {
    }

    @UiField
    MaterialModal modal;

    @UiField
    MaterialModalContent content;

    @UiField
    MaterialButton btnSave;

    private Editable elm;
    private List<EditInfo> einfos = new ArrayList<>();
    private NumberFormat noCommaFormat;

    public EditDialog() {
        this.noCommaFormat = NumberFormat.getFormat("####.##########");
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("btnClose")
    public void btnCloseHandler(ClickEvent event) {
        close();
    }

    @UiHandler("btnSave")
    public void btnSaveHandler(ClickEvent event) {
        apply();
        close();
    }

    public void open() {
        modal.open();
    }

    public void close() {
        modal.close();
    }

    public boolean isShowing() {
        return modal.isVisible();
    }

    public void setContent(Editable editable) {
        this.einfos.clear();
        this.content.clear();

        for (int i = 0;; i++) {
            this.elm = editable;
            EditInfo editInfo = this.elm.getEditInfo(i);
            if (editInfo == null) {
                break;
            }
            einfos.add(editInfo);

            if (editInfo.choice != null) {
                content.add(editInfo.choice);
                editInfo.choice.addValueChangeHandler(new ValueChangeHandler<String>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<String> event) {
                        itemStateChanged(event);
                    }
                });
            } else if (editInfo.checkbox != null) {
                content.add(editInfo.checkbox);
                editInfo.checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        itemStateChanged(event);
                    }
                });
            } else if (editInfo.texta != null) {
                editInfo.texta.setLabel(editInfo.name);
                content.add(editInfo.texta);
            } else {
                editInfo.textf = new MaterialTextBox();
                editInfo.textf.setLabel(editInfo.name);
                if (editInfo.text != null) {
                    editInfo.textf.setValue(editInfo.text);
                } else {
                    editInfo.textf.setValue(unitString(editInfo));
                }
                content.add(editInfo.textf);
            }
        }
    }

    public void apply() {
        int i;
        for (i = 0; i < einfos.size(); i++) {
            EditInfo ei = einfos.get(i);
            if (ei.textf != null && ei.text == null) {
                try {
                    double d = parseUnits(ei);
                    ei.value = d;
                } catch (Exception ex) { /* ignored */
                }
            }
            elm.setEditValue(i, ei);
        }
        Simmer.getInstance().needAnalyze();
    }

    public void itemStateChanged(GwtEvent<?> e) {
        Object src = e.getSource();
        int i;
        boolean changed = false;
        for (i = 0; i < einfos.size(); i++) {
            EditInfo ei = einfos.get(i);
            if (ei.choice == src || ei.checkbox == src) {
                elm.setEditValue(i, ei);
                if (ei.newDialog)
                    changed = true;
                Simmer.getInstance().needAnalyze();
            }
        }
        if (changed) {
           setContent(elm);
        }
    }

    private double parseUnits(EditInfo ei) throws java.text.ParseException {
        String s = ei.textf.getValue();
        s = s.trim();
        // rewrite shorthand (eg "2k2") in to normal format (eg 2.2k) using
        // regex
        s = s.replaceAll("([0-9]+)([pPnNuUmMkKgG])([0-9]+)", "$1.$3$2");
        int len = s.length();
        char uc = s.charAt(len - 1);
        double mult = 1;
        switch (uc) {
        case 'p':
        case 'P':
            mult = 1e-12;
            break;
        case 'n':
        case 'N':
            mult = 1e-9;
            break;
        case 'u':
        case 'U':
            mult = 1e-6;
            break;

        // for ohm values, we assume mega for lowercase m, otherwise milli
        case 'm':
            mult = (ei.forceLargeM) ? 1e6 : 1e-3;
            break;

        case 'k':
        case 'K':
            mult = 1e3;
            break;
        case 'M':
            mult = 1e6;
            break;
        case 'G':
        case 'g':
            mult = 1e9;
            break;
        }
        if (mult != 1)
            s = s.substring(0, len - 1).trim();
        return noCommaFormat.parse(s) * mult;
    }

    private String unitString(EditInfo ei) {
        double v = ei.value;
        double va = Math.abs(v);
        if (ei.dimensionless)
            return noCommaFormat.format(v);
        if (v == 0)
            return "0";
        if (va < 1e-9)
            return noCommaFormat.format(v * 1e12) + "p";
        if (va < 1e-6)
            return noCommaFormat.format(v * 1e9) + "n";
        if (va < 1e-3)
            return noCommaFormat.format(v * 1e6) + "u";
        if (va < 1 && !ei.forceLargeM)
            return noCommaFormat.format(v * 1e3) + "m";
        if (va < 1e3)
            return noCommaFormat.format(v);
        if (va < 1e6)
            return noCommaFormat.format(v * 1e-3) + "k";
        if (va < 1e9)
            return noCommaFormat.format(v * 1e-6) + "M";
        return noCommaFormat.format(v * 1e-9) + "G";
    }
}
