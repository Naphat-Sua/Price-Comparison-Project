import tkinter as tk
from tkinter import messagebox

def calculate_best_value():
    try:
        size_a = float(entry_size_a.get())
        price_a = float(entry_price_a.get())
        size_b = float(entry_size_b.get())
        price_b = float(entry_price_b.get())
        
        value_a = price_a / size_a
        value_b = price_b / size_b
        
        if value_a < value_b:
            result = "A has the best value"
        else:
            result = "B has the best value"
        
        result_label.config(text=result)
        
        price_difference = ((value_a - value_b) / value_a) * 100
        comparison_label.config(text=f"B is {abs(price_difference):.2f}% {'cheaper' if price_difference > 0 else 'more expensive'} than A")
    
    except ValueError:
        messagebox.showerror("Invalid input", "Please enter valid numbers for size and price.")

def reset_fields():
    entry_size_a.delete(0, tk.END)
    entry_price_a.delete(0, tk.END)
    entry_size_b.delete(0, tk.END)
    entry_price_b.delete(0, tk.END)
    result_label.config(text="")
    comparison_label.config(text="")

root = tk.Tk()
root.title("Price Comparison App")

tk.Label(root, text="Size").grid(row=0, column=1)
tk.Label(root, text="Price").grid(row=0, column=2)

tk.Label(root, text="A").grid(row=1, column=0)
entry_size_a = tk.Entry(root)
entry_size_a.grid(row=1, column=1)
entry_price_a = tk.Entry(root)
entry_price_a.grid(row=1, column=2)

tk.Label(root, text="B").grid(row=2, column=0)
entry_size_b = tk.Entry(root)
entry_size_b.grid(row=2, column=1)
entry_price_b = tk.Entry(root)
entry_price_b.grid(row=2, column=2)

calculate_button = tk.Button(root, text="Calculate", command=calculate_best_value)
calculate_button.grid(row=3, column=1, pady=10)

reset_button = tk.Button(root, text="Reset", command=reset_fields)
reset_button.grid(row=3, column=2, pady=10)

result_label = tk.Label(root, text="")
result_label.grid(row=4, column=0, columnspan=3)

comparison_label = tk.Label(root, text="")
comparison_label.grid(row=5, column=0, columnspan=3)

root.mainloop()
