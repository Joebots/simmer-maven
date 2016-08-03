package com.joebotics.simmer.client.integration;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.util.Rectangle;

/**
 * Created by joe on 7/23/16.
 */
public class CircuitComponent extends Identifiable {

    private Class<? extends AbstractCircuitElement> aceClass;
    private Rectangle bounds;
    private int[] posts;

    public CircuitComponent(){}

    public CircuitComponent(AbstractCircuitElement e){
        this.uuid = e.getUuid();
        this.aceClass = e.getClass();
        this.bounds = e.getBoundingBox();
        this.posts = new int[e.getPostCount()];

        for( int i=0; i<posts.length; i++ )
            posts[i] = i;
    }

    public int[] getPosts(){
        return posts;
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public String getComponentClassName(){
        return aceClass.getName();
    }

    public Class<? extends AbstractCircuitElement> getComponentClass(){
        return aceClass;
    }
}
