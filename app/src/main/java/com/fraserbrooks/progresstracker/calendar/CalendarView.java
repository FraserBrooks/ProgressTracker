package com.fraserbrooks.progresstracker.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.customviews.ColorUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import static com.fraserbrooks.progresstracker.calendar.CalendarPageAdapter.CALENDAR_SIZE;

@SuppressWarnings("RedundantCast")
public class CalendarView extends LinearLayout {

    private final String TAG = "CalendarView";

    private Context mContext;

    private TextView mCurrentMonthLabel;
    private int mCurrentPage;

    private CalendarViewPager mViewPager;
    private CalendarPageAdapter mCalendarPageAdapter;

    private ImageButton mForwardButton, mPreviousButton;

    private boolean mHideForwardButton = false, mHidePreviousButton = false;

    /** Start in the middle of possible range */
    private static final int STARTING_PAGE = CALENDAR_SIZE/2;

    private CalendarSettings mCalendarSettings;

    private boolean mButtonsEnabled = true;

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSettingsAndInflate(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        initSettingsAndInflate(context, attrs);
    }

    public void setTargetOneDays(Set<Date> days){
        if(mCalendarSettings != null){
            mCalendarSettings.setTargetOneDays(days);
        }
    }

    public void setTargetTwoDays(Set<Date> days){
        if(mCalendarSettings != null){
            mCalendarSettings.setTargetTwoDays(days);
        }
    }

    public void setTargetThreeDays(Set<Date> days){
        if(mCalendarSettings != null){
            mCalendarSettings.setTargetThreeDays(days);
        }
    }

    public void notifyDateChange(Date date){
        if(mCalendarPageAdapter != null){
            mCalendarPageAdapter.notifyChange(date);
        }
    }

    public void notifyDataSetChanged(){
        mCalendarPageAdapter.notifyDataSetChanged();
    }

    public void disableButtons(){
        
        mButtonsEnabled = false;

        mForwardButton = (ImageButton) findViewById(R.id.forwardButton);
        mPreviousButton = (ImageButton) findViewById(R.id.previousButton);

        mForwardButton.setVisibility(INVISIBLE);
        mPreviousButton.setVisibility(INVISIBLE);

    }

    public void enableButtons(){
        mButtonsEnabled = true;

        if(!mHideForwardButton) mForwardButton.setVisibility(VISIBLE);
        if(!mHidePreviousButton) mPreviousButton.setVisibility(VISIBLE);

    }

    private void initSettingsAndInflate(Context context, AttributeSet attrs){

        mContext = context;
        mCalendarSettings = new CalendarSettings(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert (inflater != null);
        inflater.inflate(R.layout.calendar_view, this);

        initUiElements();
        setAttributes(attrs);
        initCalendar();

    }

    /**
     * This method set xml values for calendar elements
     *
     * @param attrs A set of xml attributes
     */
    private void setAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            initCalendarSettings(typedArray);
            initAttributes();
        } finally {
            typedArray.recycle();
        }
    }

    private void initCalendarSettings(TypedArray typedArray){
        
        int headerColor = typedArray.getColor(R.styleable.CalendarView_header_color, 0);
        mCalendarSettings.setHeaderColor(headerColor);

        int headerLabelColor = typedArray.getColor(R.styleable.CalendarView_header_label_color, 0);
        mCalendarSettings.setHeaderLabelColor(headerLabelColor);

        int abbreviationsBarColor = typedArray.getColor(R.styleable.CalendarView_abbreviations_bar_color, 0);
        mCalendarSettings.setAbbreviationsBarColor(abbreviationsBarColor);

        int abbreviationsLabelsColor = typedArray.getColor(R.styleable.CalendarView_abbreviations_labels_color, 0);
        mCalendarSettings.setAbbreviationsLabelsColor(abbreviationsLabelsColor);

        int pagesColor = typedArray.getColor(R.styleable.CalendarView_pages_color, 0);
        mCalendarSettings.setPagesColor(pagesColor);

        int daysLabelsColor = typedArray.getColor(R.styleable.CalendarView_days_label_color, 0);
        mCalendarSettings.setDaysLabelsColor(daysLabelsColor);

        int anotherMonthsDaysLabelsColor = typedArray.getColor(R.styleable.CalendarView_another_months_day_labels_color, 0);
        mCalendarSettings.setAnotherMonthsDaysLabelsColor(anotherMonthsDaysLabelsColor);

        int todayLabelColor = typedArray.getColor(R.styleable.CalendarView_today_label_color, 0);
        mCalendarSettings.setTodayLabelColor(todayLabelColor);

        int dayBackgroundColor = typedArray.getColor(R.styleable.CalendarView_day_background_color,
                mContext.getResources().getColor(R.color.transparent));
        mCalendarSettings.setDayBackgroundColor(dayBackgroundColor);

        Drawable previousButtonSrc = typedArray.getDrawable(R.styleable.CalendarView_previous_button_src);
        mCalendarSettings.setPreviousButtonSrc(previousButtonSrc);

        Drawable forwardButtonSrc = typedArray.getDrawable(R.styleable.CalendarView_forward_button_src);
        mCalendarSettings.setForwardButtonSrc(forwardButtonSrc);

        Drawable targetOneSrc = typedArray.getDrawable(R.styleable.CalendarView_target_one_icon_src);
        if(targetOneSrc == null){
            targetOneSrc = ColorUtils.getGradientDrawable(getContext(),
                    ColorUtils.getLevelDefinedColor(getContext(), 3));
        }
        mCalendarSettings.setTarget1Resource(targetOneSrc);


        Drawable targetTwoSrc = typedArray.getDrawable(R.styleable.CalendarView_target_two_icon_src);
        if(targetTwoSrc == null){
            targetTwoSrc = ColorUtils.getGradientDrawable(getContext(),
                    ColorUtils.getLevelDefinedColor(getContext(), 4));
        }
        mCalendarSettings.setTarget2Resource(targetTwoSrc);

        Drawable targetThreeSrc = typedArray.getDrawable(R.styleable.CalendarView_target_three_icon_src);
        if(targetThreeSrc == null){
            targetThreeSrc = ColorUtils.getGradientDrawable(getContext(),
                    ColorUtils.getLevelDefinedColor(getContext(), 6));
        }
        mCalendarSettings.setTarget3Resource(targetThreeSrc);


    }

    private void initAttributes(){

        CalendarUtils.setHeaderColor(getRootView(), mCalendarSettings.getHeaderColor());

        CalendarUtils.setHeaderLabelColor(getRootView(), mCalendarSettings.getHeaderLabelColor());

        CalendarUtils.setAbbreviationsLabelsColor(getRootView(), mCalendarSettings.getAbbreviationsLabelsColor());

        CalendarUtils.setAbbreviationsBarColor(getRootView(), mCalendarSettings.getAbbreviationsBarColor());

        CalendarUtils.setPagesColor(getRootView(), mCalendarSettings.getPagesColor());

        CalendarUtils.setPreviousButtonImage(getRootView(), mCalendarSettings.getPreviousButtonSrc());

        CalendarUtils.setForwardButtonImage(getRootView(), mCalendarSettings.getForwardButtonSrc());

        mCalendarSettings.setItemLayoutResource(R.layout.calendar_view_day);

    }

    private void initUiElements() {

        mForwardButton = (ImageButton) findViewById(R.id.forwardButton);
        mForwardButton.setOnClickListener(onNextClickListener);

        mPreviousButton = (ImageButton) findViewById(R.id.previousButton);
        mPreviousButton.setOnClickListener(onPreviousClickListener);
        
        mCurrentMonthLabel = (TextView) findViewById(R.id.currentDateLabel);
        
        mViewPager = (CalendarViewPager) findViewById(R.id.calendarViewPager);
    }

    private final OnClickListener onNextClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mButtonsEnabled){
                Log.d(TAG, "onClick: nextClicked current = " + mViewPager.getCurrentItem());

                Calendar calendar = (Calendar) mCalendarSettings.getCurrentDate().clone();
                calendar.add(Calendar.MONTH, mViewPager.getCurrentItem());

                if(!calendar.equals(mCalendarSettings.getMaximumDate())
                        && !(mViewPager.getCurrentItem() >= mCalendarPageAdapter.getCount()-1)){

                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);

                    if(calendar.equals(mCalendarSettings.getMaximumDate())
                            || (mViewPager.getCurrentItem() >= mCalendarPageAdapter.getCount()-1)){
                        mHideForwardButton = true;
                        mForwardButton.setVisibility(View.INVISIBLE);
                    }
                    mHidePreviousButton = false;

                }


            }
        }
    };


    private final OnClickListener onPreviousClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mButtonsEnabled){
                Log.d(TAG, "onClick: backClicked current = " + mViewPager.getCurrentItem());

                Calendar calendar = (Calendar) mCalendarSettings.getCurrentDate().clone();
                calendar.add(Calendar.MONTH, mViewPager.getCurrentItem());

                if(!calendar.equals(mCalendarSettings.getMinimumDate())
                        && !(mViewPager.getCurrentItem() == 0)){

                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() -1);

                    if(calendar.equals(mCalendarSettings.getMinimumDate())
                            || (mViewPager.getCurrentItem() == 0)){
                        mHidePreviousButton = true;
                        mPreviousButton.setVisibility(INVISIBLE);
                    }
                    mHideForwardButton = false;
                }
            }
        }
    };


    private void initCalendar(){

        mCalendarPageAdapter = new CalendarPageAdapter(mContext, mCalendarSettings);

        mViewPager.setAdapter(mCalendarPageAdapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);

        mViewPager.setCurrentItem(STARTING_PAGE);
        setDate(CalendarUtils.getCalendar());
    }

    public void setOnPreviousPageChangeListener(OnCalendarPageChangeListener listener) {
        mCalendarSettings.setOnPreviousPageChangeListener(listener);
    }

    public void setOnForwardPageChangeListener(OnCalendarPageChangeListener listener) {
        mCalendarSettings.setOnForwardPageChangeListener(listener);
    }

    public static String calendarToTimeStamp(Calendar cal){

        if(cal == null)return null;

        //we want to store by day, so we shave off the time information
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);

        return format1.format(cal.getTime());
    }

    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        /**
         * This method set calendar header label
         *
         * @param position Current ViewPager position
         * @see ViewPager.OnPageChangeListener
         */
        @Override
        public void onPageSelected(int position) {
            Calendar calendar = (Calendar) mCalendarSettings.getCurrentDate().clone();
            calendar.add(Calendar.MONTH, position);

            if (!isScrollingLimited(calendar, position)) {
                setHeaderName(calendar, position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private boolean isScrollingLimited(Calendar calendar, int position) {
        if (CalendarUtils.isMonthBefore(calendar, mCalendarSettings.getMinimumDate())) {
            mViewPager.setCurrentItem(position + 1);
            return true;
        }

        if (CalendarUtils.isMonthAfter(calendar, mCalendarSettings.getMaximumDate())) {
            mViewPager.setCurrentItem(position - 1);
            return true;
        }

        return false;
    }

    private void setHeaderName(Calendar calendar, int position) {
        mCurrentMonthLabel.setText(CalendarUtils.getMonthAndYearDate(mContext, calendar));
        callOnPageChangeListeners(position);
    }

    // This method calls page change listeners after swipe calendar or click arrow buttons
    private void callOnPageChangeListeners(int position) {
        if (position > mCurrentPage && mCalendarSettings.getOnForwardPageChangeListener() != null) {
            mCalendarSettings.getOnForwardPageChangeListener().onChange();
        }

        if (position < mCurrentPage && mCalendarSettings.getOnPreviousPageChangeListener() != null) {
            mCalendarSettings.getOnPreviousPageChangeListener().onChange();
        }

        mCurrentPage = position;
    }

    /**
     * @param onDayClickListener OnDayClickListener interface responsible for handle clicks on calendar cells
     * @see OnDayClickListener
     */
    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mCalendarSettings.setOnDayClickListener(onDayClickListener);
    }

    /**
     * This method set a current and selected date of the calendar using Calendar object.
     *
     * @param date A Calendar object representing a date to which the calendar will be set
     */
    public void setDate(Calendar date)  throws IllegalArgumentException {
        if (mCalendarSettings.getMinimumDate() != null && date.before(mCalendarSettings.getMinimumDate())) {
            throw new IllegalArgumentException("SET DATE EXCEEDS THE MINIMUM DATE");
        }

        if (mCalendarSettings.getMaximumDate() != null && date.after(mCalendarSettings.getMaximumDate())) {
            throw new IllegalArgumentException("SET DATE EXCEEDS THE MAXIMUM DATE");
        }

        
        CalendarUtils.setMidnight(date);

        mCalendarSettings.getCurrentDate().setTime(date.getTime());
        mCalendarSettings.getCurrentDate().add(Calendar.MONTH, -STARTING_PAGE);
        mCurrentMonthLabel.setText(CalendarUtils.getMonthAndYearDate(mContext, date));


        mViewPager.setCurrentItem(STARTING_PAGE);
        mCalendarPageAdapter.notifyDataSetChanged();

    }

    /**
     * This method set a current and selected date of the calendar using Date object.
     *
     * @param currentDate A date to which the calendar will be set
     */
    public void setDate(Date currentDate) throws IllegalArgumentException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        setDate(calendar);
    }

    /**
     * @return Calendar object representing a date of current calendar page
     */
    public Calendar getCurrentPageDate() {
        Calendar calendar = (Calendar) mCalendarSettings.getCurrentDate().clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, mViewPager.getCurrentItem());

        Log.d(TAG, "getCurrentPageDate: returning: " + calendarToTimeStamp(calendar));

        return calendar;
    }

    /**
     * This method set a minimum available date in calendar
     *
     * @param calendar Calendar object representing a minimum date
     */
    public void setMinimumDate(Calendar calendar) {
        mCalendarSettings.setMinimumDate(calendar);
    }

    /**
     * This method set a maximum available date in calendar
     *
     * @param calendar Calendar object representing a maximum date
     */
    public void setMaximumDate(Calendar calendar) {
        mCalendarSettings.setMaximumDate(calendar);
    }

    /**
     * This method is used to return to current month page
     */
    public void showCurrentMonthPage() {
        mViewPager.setCurrentItem(STARTING_PAGE, true);
    }


}
