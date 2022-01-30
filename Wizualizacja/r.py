import itertools as it


def Compute_F(self):
    self.adapt = 0
    for i in it.combinations(self.Get_Chromosome(), 2):
        i = sorted(i, key=lambda x: x.Get_x(), reverse=False)


        if i[1].Get_x() + i[1].Get_L() > i[0].Get_x() + i[0].Get_L():
            if i[1].Get_x() < i[0].Get_x() + i[0].Get_L():
                P_p = i[0].Get_x() + i[0].Get_L() - i[1].Get_x()
                # print(f"p_p = {P_p}")
                self.adapt += 2 - (P_p / (P_p + i[0].Get_L())) - (P_p / (P_p + i[1].Get_L()))
            else:
                self.adapt += 2
                pass
                # print(f"p_p = 0")
        else:
            P_p = min([i[0].Get_L(), i[1].Get_L()])
            # print(f"p_p = {P_p}")
            self.adapt += 2 - (P_p / (P_p + i[0].Get_L())) - (P_p / (P_p + i[1].Get_L()))
    return round(self.adapt / len(self.Get_Chromosome()), 2)
