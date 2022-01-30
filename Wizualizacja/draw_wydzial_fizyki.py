import matplotlib.pyplot as plt
import matplotlib.animation as animation

x_banner, y_banner, x_fasada, y_fasada = [], [], [], []

with open(r"C:\Users\uqern\Desktop\AG_individual\a_0.txt") as fobj:
    for line in fobj:
        row = line.split()
        x_banner.append(row[0])
        y_banner.append(row[1])

with open(r"C:\Users\uqern\Desktop\AG_individual\t_0.txt") as fobj:
    for line in fobj:
        row = line.split()
        x_fasada.append(row[0])
        y_fasada.append(row[1])

fig = plt.figure(figsize=(10, 6))


x_banner = list(map(float, x_banner))
y_banner = list(map(float, y_banner))
x_fasada = list(map(float, x_fasada))
y_fasada = list(map(float, y_fasada))


ln, = plt.plot(x_banner, y_banner, 'o', color="orange", markersize=1)
plt.plot(x_fasada, y_fasada, 'o', color="black", markersize=1)

plt.xlabel('x [cm]')
plt.ylabel('y [cm]', color='k')
plt.xlim(-300, 3000)
plt.ylim(-50, 2000)


def update(i):
    print(i)
    x_banner.clear()
    y_banner.clear()

    with open(r"C:\Users\uqern\Desktop\AG_individual\a_" + str(i) + ".txt") as fobj:
        for line in fobj:
            row = line.split()
            x_banner.append(row[0])
            y_banner.append(row[1])
    ln.set_data(list(map(float, x_banner)), list(map(float, y_banner)))
    return ln

ani = animation.FuncAnimation(fig, update, repeat=True, frames=9, interval=99, blit=False)
f = r"C:\Users\uqern\Desktop\AG_individual\animation.gif"
writergif = animation.PillowWriter()
ani.save(f, writer=writergif)