/*
 * SVG Salamander
 * Copyright (c) 2004, Mark McKay
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   - Redistributions of source code must retain the above 
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Mark McKay can be contacted at mark@kitfox.com.  Salamander and other
 * projects can be found at http://www.kitfox.com
 *
 * Created on September 28, 2004, 10:07 PM
 */


package com.kitfox.svg.app;

import java.util.*;

/**
 *
 * @author  kitfox
 */
public class PlayerThread implements Runnable
{
    HashSet listeners = new HashSet();
    
    double curTime = 0;
    double timeStep = .2;
    
    public static final int PS_STOP = 0;
    public static final int PS_PLAY_FWD = 1;
    public static final int PS_PLAY_BACK = 2;
    
    int playState = PS_STOP;
    
    Thread thread;
    
    /** Creates a new instance of PlayerThread */
    public PlayerThread()
    {
        thread = new Thread(this);
        thread.start();
    }
    
    public void run()
    {
        while (thread != null)
        {
            synchronized (this)
            {
                switch (playState)
                {
                    case PS_PLAY_FWD:
                        curTime += timeStep;
                        break;
                    case PS_PLAY_BACK:
                        curTime -= timeStep;
                        if (curTime < 0) curTime = 0;
                        break;
                    default:
                    case PS_STOP:
                        break;
                }
                
                fireTimeUpdateEvent();
            }
            
            try
            {
                Thread.sleep((long)(timeStep * 1000));
            }
            catch (Exception e) 
            { 
                throw new RuntimeException(e); 
            }
        }
    }
    
    public void exit() { thread = null; }
    public synchronized void addListener(PlayerThreadListener listener) 
    {
        listeners.add(listener); 
    }
    
    public synchronized double getCurTime() { return curTime; }
    
    public synchronized void setCurTime(double time)
    {
        curTime = time;
    }
    
    public synchronized double getTimeStep() { return timeStep; }
    
    public synchronized void setTimeStep(double time)
    {
        timeStep = time;
        if (timeStep < .01) timeStep = .01;
    }
    
    public synchronized int getPlayState() { return playState; }
    
    public synchronized void setPlayState(int playState)
    {
        this.playState = playState;
    }
    
    private void fireTimeUpdateEvent()
    {
        for (Iterator it = listeners.iterator(); it.hasNext();)
        {
            PlayerThreadListener listener = (PlayerThreadListener)it.next();
            listener.updateTime(curTime, timeStep, playState);
        }
    }
}
