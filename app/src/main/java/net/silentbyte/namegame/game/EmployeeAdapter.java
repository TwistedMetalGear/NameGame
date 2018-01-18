package net.silentbyte.namegame.game;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.silentbyte.namegame.R;
import net.silentbyte.namegame.data.Employee;
import net.silentbyte.namegame.databinding.ListItemEmployeeBinding;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private final List<Employee> employees = new ArrayList<>();
    private final NameGameViewModel viewModel;

    public EmployeeAdapter(NameGameViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemEmployeeBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_employee, parent, false);
        binding.setViewModel(viewModel);
        return new EmployeeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        holder.binding.setEmployee(employees.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public void setEmployees(List<? extends Employee> employees) {
        this.employees.clear();
        this.employees.addAll(employees);
        notifyDataSetChanged();
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {

        ListItemEmployeeBinding binding;

        public EmployeeViewHolder(ListItemEmployeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
