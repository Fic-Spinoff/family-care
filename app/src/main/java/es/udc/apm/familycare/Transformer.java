package es.udc.apm.familycare;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * Created by Gonzalo on 18/06/2017.
 */

public class Transformer implements ViewPager.PageTransformer {

    private PageOne firstPage;
    private PageTwo secondPage;

    public Transformer(PageOne firstPage, PageTwo secondPage) {
        this.firstPage = firstPage;
        this.secondPage = secondPage;
    }

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(1);

        } else if (position <= 1) { // [-1,1]
            if(view.getId() == R.id.layout_page_two) {
                Log.d("TRANS", "PAGE 1: "+position);
                //First page move
                firstPage.imgCloud1.setTranslationX(-(1 - position) * 0.1f * pageWidth);
                firstPage.imgCloud2.setTranslationX(-(1 - position) * 0.3f * pageWidth);
                firstPage.imgCloud3.setTranslationX(-(1 - position) * 0.5f * pageWidth);
                firstPage.imgCloud4.setTranslationX(-(1 - position) * 0.7f * pageWidth);
                firstPage.tvTrack1.setTranslationX(-(1 - position) * pageWidth * 0.5f);
                firstPage.tvTrack2.setTranslationX(-(1 - position) * pageWidth * 0.7f);

                //Second page
                if(position >= 0) {
                    secondPage.bg.setAlpha(1 - position);
                    secondPage.imgFall.setTranslationY(-position * 200);
                    secondPage.tvFall1.setTranslationX(position * pageWidth * 0.5f);
                    secondPage.tvFall2.setTranslationX(position * pageWidth * 0.7f);
                }
                else {
                    secondPage.bg.setAlpha(1 + position);
                    secondPage.imgFall.setTranslationY(position * 60);
                    secondPage.tvFall1.setTranslationX(position * pageWidth * 0.5f);
                    secondPage.tvFall2.setTranslationX(position * pageWidth * 0.7f);
                }
            }

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(1);
        }
    }
}
