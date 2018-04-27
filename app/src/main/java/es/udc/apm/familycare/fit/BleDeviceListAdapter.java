package es.udc.apm.familycare.fit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.fitness.data.BleDevice;

import java.util.ArrayList;

import es.udc.apm.familycare.R;


class BleDeviceListAdapter extends BaseAdapter {
    private FitActivity mFitActivity;
    private ArrayList<BleDevice> mLeDevices;
    private LayoutInflater mInflator;

    public BleDeviceListAdapter(FitActivity fitActivity) {
        super();
        mFitActivity = fitActivity;
        mLeDevices = new ArrayList<>();
        mInflator = mFitActivity.getLayoutInflater();
    }

    public void addDevice(BleDevice device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BleDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = view.findViewById(R.id.device_address);
            viewHolder.deviceName = view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BleDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

}
