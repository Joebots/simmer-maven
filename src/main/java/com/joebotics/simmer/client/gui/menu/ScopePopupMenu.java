package com.joebotics.simmer.client.gui.menu;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.joebotics.simmer.client.gui.widget.CheckboxAlignedMenuItem;
import com.joebotics.simmer.client.gui.widget.CheckboxMenuItem;
import com.joebotics.simmer.client.gui.Scope;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 7/17/16.
 */
public class ScopePopupMenu extends MenuBar{
    private Scope scopes[];

    private MenuBar                         scopeMenuBar;
    private MenuItem                        scopeSelectYMenuItem;
    private CheckboxMenuItem scopeFreqMenuItem;
    private CheckboxMenuItem				scopeIbMenuItem;
    private CheckboxMenuItem				scopeIcMenuItem;
    private CheckboxMenuItem				scopeIeMenuItem;
    private CheckboxMenuItem				scopeIMenuItem;
    private CheckboxMenuItem				scopeMaxMenuItem;
    private CheckboxMenuItem				scopeMinMenuItem;
    private CheckboxMenuItem				scopePowerMenuItem;
    private CheckboxMenuItem				scopeResistMenuItem;
    private CheckboxMenuItem				scopeScaleMenuItem;
    private CheckboxMenuItem				scopeVbcMenuItem;
    private CheckboxMenuItem				scopeVbeMenuItem;
    private CheckboxMenuItem				scopeVceIcMenuItem;
    private CheckboxMenuItem				scopeVceMenuItem;
    private CheckboxMenuItem				scopeVIMenuItem;
    private CheckboxMenuItem				scopeVMenuItem;
    private CheckboxMenuItem				scopeXYMenuItem;

    public ScopePopupMenu(boolean t) {
        super(true);

        MenuBar m = this;
        m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getMessage("Remove"), new MenuCommand("scopepop", "remove")));
        m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getMessage("Speed_2x"), new MenuCommand("scopepop", "speed2")));
        m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getMessage("Speed_1/2x"), new MenuCommand("scopepop", "speed1/2")));
        m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getMessage("Scale_2x"), new MenuCommand("scopepop", "scale")));
        m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getMessage("Max_Scale"), new MenuCommand("scopepop", "maxscale")));
        m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getMessage("Stack"), new MenuCommand("scopepop", "stack")));
        m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getMessage("Unstack"), new MenuCommand("scopepop", "unstack")));
        m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getMessage("Reset"), new MenuCommand("scopepop", "reset")));
        if (t) {
            m.addItem(scopeIbMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Ib"), new MenuCommand("scopepop", "showib")));
            m.addItem(scopeIcMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Ic"), new MenuCommand("scopepop", "showic")));
            m.addItem(scopeIeMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Ie"), new MenuCommand("scopepop", "showie")));
            m.addItem(scopeVbeMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Vbe"), new MenuCommand("scopepop", "showvbe")));
            m.addItem(scopeVbcMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Vbc"), new MenuCommand("scopepop", "showvbc")));
            m.addItem(scopeVceMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Vce"), new MenuCommand("scopepop", "showvce")));
            m.addItem(scopeVceIcMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Vce_vs_Ic"), new MenuCommand("scopepop", "showvcevsic")));
        } else {
            m.addItem(scopeVMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Voltage"), new MenuCommand("scopepop", "showvoltage")));
            m.addItem(scopeIMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Current"), new MenuCommand("scopepop", "showcurrent")));
            m.addItem(scopePowerMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Power_Consumed"), new MenuCommand("scopepop", "showpower")));
            m.addItem(scopeScaleMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Scale"), new MenuCommand("scopepop", "showscale")));
            m.addItem(scopeMaxMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Peak_Value"), new MenuCommand("scopepop", "showpeak")));
            m.addItem(scopeMinMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Negative_Peak_Value"), new MenuCommand("scopepop", "shownegpeak")));
            m.addItem(scopeFreqMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Frequency"), new MenuCommand("scopepop", "showfreq")));
            m.addItem(scopeVIMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_V_vs_I"), new MenuCommand("scopepop", "showvvsi")));
            m.addItem(scopeXYMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Plot_X/Y"), new MenuCommand("scopepop", "plotxy")));
            m.addItem(scopeSelectYMenuItem = new CheckboxAlignedMenuItem(MessageI18N.getMessage("Select_Y"), new MenuCommand("scopepop", "selecty")));
            m.addItem(scopeResistMenuItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Resistance"), new MenuCommand("scopepop", "showresistance")));
        }
    }

    public Scope[] getScopes() {
        return scopes;
    }

    public MenuBar getScopeMenuBar() {
        return scopeMenuBar;
    }

    public MenuItem getScopeSelectYMenuItem() {
        return scopeSelectYMenuItem;
    }

    public CheckboxMenuItem getScopeFreqMenuItem() {
        return scopeFreqMenuItem;
    }

    public CheckboxMenuItem getScopeIbMenuItem() {
        return scopeIbMenuItem;
    }

    public CheckboxMenuItem getScopeIcMenuItem() {
        return scopeIcMenuItem;
    }

    public CheckboxMenuItem getScopeIeMenuItem() {
        return scopeIeMenuItem;
    }

    public CheckboxMenuItem getScopeIMenuItem() {
        return scopeIMenuItem;
    }

    public CheckboxMenuItem getScopeMaxMenuItem() {
        return scopeMaxMenuItem;
    }

    public CheckboxMenuItem getScopeMinMenuItem() {
        return scopeMinMenuItem;
    }

    public CheckboxMenuItem getScopePowerMenuItem() {
        return scopePowerMenuItem;
    }

    public CheckboxMenuItem getScopeResistMenuItem() {
        return scopeResistMenuItem;
    }

    public CheckboxMenuItem getScopeScaleMenuItem() {
        return scopeScaleMenuItem;
    }

    public CheckboxMenuItem getScopeVbcMenuItem() {
        return scopeVbcMenuItem;
    }

    public CheckboxMenuItem getScopeVbeMenuItem() {
        return scopeVbeMenuItem;
    }

    public CheckboxMenuItem getScopeVceIcMenuItem() {
        return scopeVceIcMenuItem;
    }

    public CheckboxMenuItem getScopeVceMenuItem() {
        return scopeVceMenuItem;
    }

    public CheckboxMenuItem getScopeVIMenuItem() {
        return scopeVIMenuItem;
    }

    public CheckboxMenuItem getScopeVMenuItem() {
        return scopeVMenuItem;
    }

    public CheckboxMenuItem getScopeXYMenuItem() {
        return scopeXYMenuItem;
    }
}
