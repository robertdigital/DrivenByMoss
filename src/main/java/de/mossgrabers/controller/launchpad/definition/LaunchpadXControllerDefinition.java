// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.definition;

import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


/**
 * Definition class for the Novation Launchpad X controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadXControllerDefinition extends DefaultControllerDefinition implements ILaunchpadControllerDefinition
{
    private static final UUID   EXTENSION_ID                  = UUID.fromString ("CD196CCF-DF98-4AB0-9ABC-F0F29A60ACED");
    private static final String SYSEX_HEADER                  = "F0 00 20 29 02 0C ";

    private static final int    LAUNCHPAD_BUTTON_UP           = 91;
    private static final int    LAUNCHPAD_BUTTON_DOWN         = 92;
    private static final int    LAUNCHPAD_BUTTON_LEFT         = 93;
    private static final int    LAUNCHPAD_BUTTON_RIGHT        = 94;
    private static final int    LAUNCHPAD_BUTTON_SESSION      = 95;
    private static final int    LAUNCHPAD_BUTTON_NOTE         = 96;
    private static final int    LAUNCHPAD_BUTTON_CUSTOM       = 97;
    private static final int    LAUNCHPAD_BUTTON_CAPTURE_MIDI = 98;


    /**
     * Constructor.
     */
    public LaunchpadXControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchpad X", "Novation", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createWindowsDeviceDiscoveryPairs ("MIDIIN2 (%sLPX MIDI)", "MIDIOUT2 (%sLPX MIDI)"));
                break;

            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad X MIDI 2", "Launchpad X MIDI 2"));
                break;

            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad X LPX MIDI Out", "Launchpad X LPX MIDI In"));
                break;

            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPro ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasFaderSupport ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getSysExHeader ()
    {
        return SYSEX_HEADER;
    }


    /** {@inheritDoc} */
    @Override
    public String getStandaloneModeCommand ()
    {
        return "10 00";
    }


    /** {@inheritDoc} */
    @Override
    public String getProgramModeCommand ()
    {
        return "0E 01";
    }


    /** {@inheritDoc} */
    @Override
    public String getFaderModeCommand ()
    {
        return this.getProgramModeCommand ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPanModeCommand ()
    {
        return this.getProgramModeCommand ();
    }


    /** {@inheritDoc} */
    @Override
    public void sendBlinkState (final IMidiOutput output, final int note, final int blinkColor, final boolean fast)
    {
        // Start blinking on channel 2, stop it on channel 1
        output.sendNoteEx (blinkColor == 0 ? 1 : 2, note, blinkColor);
    }


    /** {@inheritDoc} */
    @Override
    public Map<ButtonID, Integer> getButtonIDs ()
    {
        final Map<ButtonID, Integer> buttonIDs = new EnumMap<> (ButtonID.class);
        buttonIDs.put (ButtonID.SHIFT, Integer.valueOf (LAUNCHPAD_BUTTON_CAPTURE_MIDI));

        buttonIDs.put (ButtonID.LEFT, Integer.valueOf (LAUNCHPAD_BUTTON_LEFT));
        buttonIDs.put (ButtonID.RIGHT, Integer.valueOf (LAUNCHPAD_BUTTON_RIGHT));
        buttonIDs.put (ButtonID.UP, Integer.valueOf (LAUNCHPAD_BUTTON_UP));
        buttonIDs.put (ButtonID.DOWN, Integer.valueOf (LAUNCHPAD_BUTTON_DOWN));

        buttonIDs.put (ButtonID.SESSION, Integer.valueOf (LAUNCHPAD_BUTTON_SESSION));
        buttonIDs.put (ButtonID.NOTE, Integer.valueOf (LAUNCHPAD_BUTTON_NOTE));
        buttonIDs.put (ButtonID.DEVICE, Integer.valueOf (LAUNCHPAD_BUTTON_CUSTOM));

        buttonIDs.put (ButtonID.SCENE1, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1));
        buttonIDs.put (ButtonID.SCENE2, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2));
        buttonIDs.put (ButtonID.SCENE3, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3));
        buttonIDs.put (ButtonID.SCENE4, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4));
        buttonIDs.put (ButtonID.SCENE5, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5));
        buttonIDs.put (ButtonID.SCENE6, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6));
        buttonIDs.put (ButtonID.SCENE7, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7));
        buttonIDs.put (ButtonID.SCENE8, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8));
        return buttonIDs;
    }


    /** {@inheritDoc} */
    @Override
    public boolean sceneButtonsUseCC ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public List<String> buildLEDUpdate (final Map<Integer, LightInfo> padInfos)
    {
        final StringBuilder sb = new StringBuilder (this.getSysExHeader ()).append ("03 ");
        for (final Entry<Integer, LightInfo> e: padInfos.entrySet ())
        {
            final int note = e.getKey ().intValue ();
            final LightInfo info = e.getValue ();

            if (info.getBlinkColor () <= 0)
            {
                // 00h: Static colour from palette, Lighting data is 1 byte specifying palette
                // entry.
                sb.append ("00 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
            }
            else
            {
                if (info.isFast ())
                {
                    // 01h: Flashing colour, Lighting data is 2 bytes specifying Colour B and
                    // Colour A.
                    sb.append ("01 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getBlinkColor ())).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
                }
                else
                {
                    // 02h: Pulsing colour, Lighting data is 1 byte specifying palette entry.
                    sb.append ("02 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
                }
            }
        }
        return Collections.singletonList (sb.append ("F7").toString ());
    }
}
